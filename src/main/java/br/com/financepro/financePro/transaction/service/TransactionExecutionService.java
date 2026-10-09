package br.com.financepro.financePro.transaction.service;

import br.com.financepro.financePro.common.enums.RecurrenceType;
import br.com.financepro.financePro.common.enums.TransactionStatus;
import br.com.financepro.financePro.common.enums.TransactionType;
import br.com.financepro.financePro.common.enums.WalletType;
import br.com.financepro.financePro.installment.dto.InstallmentRequestDTO;
import br.com.financepro.financePro.installment.service.InstallmentService;
import br.com.financepro.financePro.installmentPlan.service.InstallmentPlanService;
import br.com.financepro.financePro.mapper.transaction.TransactionMapper;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.transaction.dto.request.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.response.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.service.WalletBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionExecutionService {

    private final Logger log = LoggerFactory.getLogger(TransactionExecutionService.class.getName());

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private WalletBalanceService walletBalanceService;

    @Autowired
    private TransactionMapper mapper;

    @Transactional
    public TransactionResponseDTO create(TransactionRequestDTO transaction) {
        log.info("Creating Transaction");

        Wallet wallet = walletBalanceService.getWallet(transaction.getWalletId());

        perform(wallet, transaction.getAmount(), transaction.getType(), true, false);
        return mapper.toResponse(repository.save(mapper.toEntity(transaction)));
    }

    private BigDecimal calculateTheInterest(BigDecimal total, Double fees) {
        var valueInterest = (total.multiply(BigDecimal.valueOf(fees))).divide(BigDecimal.valueOf(100));
        return total.add(valueInterest);
    }

    @Transactional
    public void createByRecurrence(Recurrence recurrence, LocalDate executionDate) {
        log.info("Creating Transaction by Recurrence");

        Transaction transaction = mapper.getTransactionByRecurrence(recurrence, executionDate);
        repository.save(transaction);

        perform(
            recurrence.getWallet(),
            recurrence.getAmount(),
            recurrence.getType() == RecurrenceType.CREDIT
                ? TransactionType.CREDIT
                : TransactionType.DEBIT,
            true,
            false
        );
    }

    private void perform(Wallet wallet, BigDecimal amount, TransactionType type, Boolean isTransaction, Boolean isDeletingTransaction) {
        if (type.name().equals("CREDIT")) {
            walletBalanceService.credit(wallet, amount, isTransaction, isDeletingTransaction);
        } else {
            walletBalanceService.debit(wallet, amount, isTransaction, isDeletingTransaction);
        }
    }
}