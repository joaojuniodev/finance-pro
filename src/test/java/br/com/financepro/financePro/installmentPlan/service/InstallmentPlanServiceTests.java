package br.com.financepro.financePro.installmentPlan.service;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.common.enums.InvoiceStatus;
import br.com.financepro.financePro.common.enums.InstallmentPlanStatus;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.installment.model.Installment;
import br.com.financepro.financePro.installment.repository.InstallmentRepository;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.model.InstallmentPlan;
import br.com.financepro.financePro.installmentPlan.repository.InstallmentPlanRepository;
import br.com.financepro.financePro.invoice.model.Invoice;
import br.com.financepro.financePro.invoice.repository.InvoiceRepository;
import br.com.financepro.financePro.invoice.service.InvoiceCalendarService;
import br.com.financepro.financePro.mapper.installment.InstallmentMapper;
import br.com.financepro.financePro.mapper.installmentPlan.InstallmentPlanMapper;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstallmentPlanServiceTests {
    @Mock InstallmentPlanRepository plans;
    @Mock InstallmentRepository installments;
    @Mock InvoiceRepository invoices;
    @Mock InstallmentPlanMapper planMapper;
    @Mock InstallmentMapper installmentMapper;
    @Mock InvoiceCalendarService calendar;
    @Mock WalletRepository wallets;
    @InjectMocks InstallmentPlanService service;

    @Test
    void distributesHistoricalPurchaseAcrossYearsWithoutChargingPaidInstallments() {
        Wallet card = new Wallet();
        card.setId(UUID.randomUUID());
        card.setType(WalletType.CREDIT_CARD);
        card.setAccount(new Account());
        Invoice anchor = new Invoice();
        anchor.setId(UUID.randomUUID());
        anchor.setWallet(card);
        anchor.setClosingDate(LocalDate.of(2026, 11, 10));
        anchor.setExpirationDate(LocalDate.of(2026, 11, 20));
        anchor.setLimit(new BigDecimal("1000.00"));
        Map<YearMonth, Invoice> cycles = new HashMap<>();
        when(invoices.findOwned(anchor.getId(), "owner")).thenReturn(Optional.of(anchor));
        when(wallets.findByIdForUpdate(card.getId())).thenReturn(Optional.of(card));
        when(calendar.ensureMonth(eq(card), eq(anchor), any())).thenAnswer(call -> cycles.computeIfAbsent(call.getArgument(2), month -> {
            Invoice invoice = new Invoice();
            invoice.setWallet(card);
            invoice.setLimit(new BigDecimal("1000.00"));
            invoice.setStatus(InvoiceStatus.PAYING);
            return invoice;
        }));
        when(planMapper.toEntity(any())).thenAnswer(call -> new InstallmentPlan());
        when(plans.save(any())).thenAnswer(call -> {
            InstallmentPlan plan = call.getArgument(0);
            plan.setId(UUID.randomUUID());
            return plan;
        });
        when(installmentMapper.toEntity(any())).thenAnswer(call -> {
            var request = (br.com.financepro.financePro.installment.dto.InstallmentRequestDTO) call.getArgument(0);
            Installment item = new Installment();
            item.setValue(request.value()); item.setPaid(request.isPaid());
            item.setMonth(request.month()); item.setYear(request.year());
            return item;
        });
        when(installments.save(any())).thenAnswer(call -> call.getArgument(0));
        var request = new InstallmentPlanRequestDTO(null, "Compra", new BigDecimal("100.00"),
            BigDecimal.valueOf(3), BigDecimal.ZERO, InstallmentPlanStatus.PAYING, anchor.getId(),
            YearMonth.of(2025, 12), List.of(1));

        service.create(request, "owner");

        verify(calendar).ensureYear(card, anchor, 2025);
        verify(calendar).ensureYear(card, anchor, 2026);
        assertEquals(new BigDecimal("1000.00"), cycles.get(YearMonth.of(2025, 12)).getLimit());
        assertEquals(new BigDecimal("966.67"), cycles.get(YearMonth.of(2026, 1)).getLimit());
        assertEquals(new BigDecimal("966.67"), cycles.get(YearMonth.of(2026, 2)).getLimit());
        verify(installments, times(3)).save(any());
    }
}
