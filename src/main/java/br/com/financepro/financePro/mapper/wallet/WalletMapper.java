package br.com.financepro.financePro.mapper.wallet;

import br.com.financepro.financePro.bank.dto.BankResponseDTO;
import br.com.financepro.financePro.bank.model.Bank;
import br.com.financepro.financePro.bank.repository.BankRepository;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.goal.model.Goal;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.goal.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper implements ObjectMapper<Wallet, WalletResponseDTO, WalletRequestDTO> {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Override
    public Wallet toEntity(WalletRequestDTO request) {
        Goal goal = request.getGoalId() != null
            ? goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new NotFoundException("Not found this Goal Id: " + request.getGoalId()))
            : null;
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        Bank bank = request.getBankId() != null
            ? bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new NotFoundException("Not found this Bank Id: " + request.getBankId()))
            : null;
        return new Wallet(
            request.getId(),
            request.getName(),
            request.getDescription(),
            request.getBalance(),
            request.getCardDigits(),
            goal,
            account,
            bank
        );
    }

    @Override
    public WalletResponseDTO toResponse(Wallet entity) {
        BankResponseDTO bank = entity.getBank() != null
            ? new BankResponseDTO(
                entity.getBank().getId(),
                entity.getBank().getName(),
                entity.getBank().getIcon(),
                entity.getBank().getColor(),
                entity.getBank().getGradient(),
                entity.getBank().getShadow())
            : null;
        return new WalletResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCardDigits(),
            entity.getBalance(),
            bank
        );
    }
}
