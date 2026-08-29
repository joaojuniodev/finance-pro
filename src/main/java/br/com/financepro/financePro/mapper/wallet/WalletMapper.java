package br.com.financepro.financePro.mapper.wallet;

import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.bank.repository.BankRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.goal.repository.GoalRepository;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.bank.BankMapper;
import br.com.financepro.financePro.transaction.dto.response.TransactionSummaryDTO;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.dto.WalletSummaryDTO;
import br.com.financepro.financePro.wallet.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class WalletMapper implements ObjectMapper<Wallet, WalletResponseDTO, WalletRequestDTO> {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BankMapper bankMapper;

    @Override
    public Wallet toEntity(WalletRequestDTO request) {
        var goal = request.getGoalId() != null
            ? goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new NotFoundException("Not found this Goal Id: " + request.getGoalId()))
            : null;
        var account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        var bank = request.getBankId() != null
            ? bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new NotFoundException("Not found this Bank Id: " + request.getBankId()))
            : null;
        return new Wallet(
            request.getId(),
            request.getName(),
            request.getDescription(),
            request.getBalance(),
            request.getCardDigits(),
            request.getType(),
            request.getColor(),
            goal,
            account,
            bank
        );
    }

    @Override
    public WalletResponseDTO toResponse(Wallet entity) {
        var bank = entity.getBank() != null
            ? bankMapper.toResponse(entity.getBank())
            : null;
        return new WalletResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getCardDigits(),
            entity.getType(),
            entity.getColor(),
            entity.getBalance(),
            bank
        );
    }

    public WalletSummaryDTO toSummary(Wallet entity, BigDecimal income, BigDecimal expense, List<TransactionSummaryDTO> transactions) {
        return new WalletSummaryDTO(
            income,
            expense,
            toResponse(entity),
            transactions
        );
    }
}
