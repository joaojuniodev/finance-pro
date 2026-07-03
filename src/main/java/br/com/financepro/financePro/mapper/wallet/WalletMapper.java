package br.com.financepro.financePro.mapper.wallet;

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

    @Override
    public Wallet toEntity(WalletRequestDTO request) {
        Goal goal = request.getGoalId() != null
            ? goalRepository.findById(request.getGoalId()).get()
            : null;
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        return new Wallet(
            request.getId(),
            request.getName(),
            request.getDescription(),
            request.getBalance(),
            goal,
            account
        );
    }

    @Override
    public WalletResponseDTO toResponse(Wallet entity) {
        return new WalletResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getBalance()
        );
    }
}
