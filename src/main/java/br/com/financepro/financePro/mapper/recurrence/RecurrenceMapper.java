package br.com.financepro.financePro.mapper.recurrence;

import br.com.financepro.financePro.recurrence.dto.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.recurrence.model.Recurrence;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecurrenceMapper implements ObjectMapper<Recurrence, RecurrenceResponseDTO, RecurrenceRequestDTO> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository waleWalletRepository;

    @Override
    public Recurrence toEntity(RecurrenceRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        Wallet wallet = waleWalletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        return new Recurrence(
            request.getId(),
            request.getAmount(),
            request.getType(),
            request.getFrequencyType(),
            request.getDayOne(),
            request.getDayTwo(),
            request.getMonthOfTheYear(),
            request.getDescription(),
            account,
            wallet
        );
    }

    @Override
    public RecurrenceResponseDTO toResponse(Recurrence entity) {
        return new RecurrenceResponseDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getType(),
            entity.getFrequencyType(),
            entity.getDayOne(),
            entity.getDayTwo(),
            entity.getMonthOfTheYear(),
            entity.getNextExecutionDate(),
            entity.getLastExecutionDate(),
            entity.getActive(),
            entity.getDescription()
        );
    }
}