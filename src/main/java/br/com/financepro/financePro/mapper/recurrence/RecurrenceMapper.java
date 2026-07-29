package br.com.financepro.financePro.mapper.recurrence;

import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.category.CategoryMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.recurrence.dto.AllRecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceSummaryDTO;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RecurrenceMapper implements ObjectMapper<Recurrence, RecurrenceResponseDTO, RecurrenceRequestDTO> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository waleWalletRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Recurrence toEntity(RecurrenceRequestDTO request) {
        var account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        var wallet = waleWalletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this Wallet Id: " + request.getAccountId()));
        var category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + request.getCategoryId()));
        return new Recurrence(
            request.getId(),
            request.getAmount(),
            request.getType(),
            request.getFrequencyType(),
            request.getExecutionType(),
            request.getDayOne(),
            request.getDayTwo(),
            request.getMonthOfTheYear(),
            request.getDescription(),
            category,
            wallet,
            account
        );
    }

    @Override
    public RecurrenceResponseDTO toResponse(Recurrence entity) {
        var category = categoryMapper.toResponse(entity.getCategory());
        var wallet = walletMapper.toResponse(entity.getWallet());
        return new RecurrenceResponseDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getType(),
            entity.getFrequencyType(),
            entity.getExecutionType(),
            entity.getDayOne(),
            entity.getDayTwo(),
            entity.getMonthOfTheYear(),
            entity.getNextExecutionDate(),
            entity.getLastExecutionDate(),
            entity.getActive(),
            entity.getDescription(),
            category,
            wallet
        );
    }

    public RecurrenceSummaryDTO toSummary(Recurrence entity) {
        return new RecurrenceSummaryDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getType(),
            entity.getDescription(),
            entity.getNextExecutionDate(),
            categoryMapper.toResponse(entity.getCategory())
        );
    }

    public AllRecurrenceResponseDTO getAllRecurrenceResponseDTO(
        Long totalRegistered,
        List<RecurrenceResponseDTO> recurrencesDueToday,
        List<RecurrenceResponseDTO> recurrencesOverdue,
        List<RecurrenceResponseDTO> recurrencesUpcoming,
        BigDecimal totalIncomeAmount,
        BigDecimal totalExpenseAmount
    ) {
        AllRecurrenceResponseDTO response = new AllRecurrenceResponseDTO();
        response.setTotalRegistered(totalRegistered);
        response.setTotalIncomeAmount(totalIncomeAmount);
        response.setTotalExpenseAmount(totalExpenseAmount);
        response.setMonthlyImpact(totalIncomeAmount.subtract(totalExpenseAmount));
        response.setRecurrencesDueToday(recurrencesDueToday);
        response.setRecurrencesOverdue(recurrencesOverdue);
        response.setRecurrencesUpcoming(recurrencesUpcoming);
        return response;
    }
}