package br.com.financepro.financePro.invoice.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.mapper.invoice.InvoiceMapper;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import br.com.financepro.financePro.wallet.service.WalletBalanceOperations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditCardInvoiceServiceTests {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private InstallmentRepository installmentRepository;

    @Mock
    private WalletBalanceOperations walletBalanceOperations;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private CreditCardInvoiceService service;

    @Test
    void shouldPayAnExpiredInvoice() {
        UUID invoiceId = UUID.randomUUID();
        UUID creditCardId = UUID.randomUUID();
        UUID paymentWalletId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal pendingAmount = new BigDecimal("150.00");

        Account account = mock(Account.class);
        Wallet creditCard = mock(Wallet.class);
        Wallet paymentWallet = mock(Wallet.class);
        Installment installment = mock(Installment.class);

        when(account.getId()).thenReturn(accountId);
        when(creditCard.getId()).thenReturn(creditCardId);
        when(creditCard.getAccount()).thenReturn(account);
        when(paymentWallet.getId()).thenReturn(paymentWalletId);
        when(paymentWallet.getAccount()).thenReturn(account);
        when(installment.getPaid()).thenReturn(false);
        when(installment.getValue()).thenReturn(pendingAmount);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus(InvoiceStatus.EXPIRED);
        invoice.setClosingDate(LocalDate.of(2026, 9, 20));
        invoice.setExpirationDate(LocalDate.of(2026, 9, 27));
        invoice.setWallet(creditCard);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(walletRepository.findById(paymentWalletId)).thenReturn(Optional.of(paymentWallet));
        when(installmentRepository.findAllByInstallmentPlanInvoiceIdAndMonthAndYear(
            invoiceId,
            9,
            2026
        )).thenReturn(List.of(installment));

        service.payBill(invoiceId, paymentWalletId);

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        verify(walletBalanceOperations).debit(paymentWallet, pendingAmount, false, false);
        verify(installment).setPaid(true);
        verify(installmentRepository).saveAll(List.of(installment));
        verify(invoiceRepository).save(invoice);
    }
}
