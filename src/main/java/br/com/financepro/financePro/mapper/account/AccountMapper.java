package br.com.financepro.financePro.mapper.account;

import br.com.financepro.financePro.account.dto.AccountRequestDTO;
import br.com.financepro.financePro.account.dto.AccountResponseDTO;
import br.com.financepro.financePro.account.dto.BiggestExpense;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.wallet.WalletMapper;
import br.com.financepro.financePro.wallet.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class AccountMapper implements ObjectMapper<Account, AccountResponseDTO, AccountRequestDTO> {

    @Autowired
    private WalletMapper walletMapper;

    @Override
    public Account toEntity(AccountRequestDTO dto) {
        return new Account(
            dto.getId(),
            dto.getCurrentBalance(),
            dto.getIncome(),
            dto.getExpenses(),
            dto.getNetIncome()
        );
    }

    @Override
    public AccountResponseDTO toResponse(Account entity) {
        final BigDecimal CURRENT_BALANCE = calculateCurrentBalance(entity.getWallets());
        final BigDecimal NET_INCOME = calculateNetIncome(entity.getIncome(), entity.getExpenses());

        var wallets = entity.getWallets()
            .stream()
            .map(wallet -> walletMapper.toResponse(wallet)).toList();

        CategoryResponseDTO category = entity.getBiggestExpenseCategory() != null
            ? new CategoryResponseDTO(
                entity.getBiggestExpenseCategory().getId(),
                entity.getBiggestExpenseCategory().getName(),
                entity.getBiggestExpenseCategory().getType(),
                entity.getBiggestExpenseCategory().getIcon(),
                entity.getBiggestExpenseCategory().getSystem())
            : null;

        return new AccountResponseDTO(
            entity.getId(),
            CURRENT_BALANCE,
            entity.getIncome(),
            entity.getExpenses(),
            NET_INCOME,
            new BiggestExpense(
                entity.getBiggestExpenseValue(),
                category
            ),
            wallets
        );
    }

    private BigDecimal calculateNetIncome(BigDecimal income, BigDecimal expenses) {
        return income.subtract(expenses);
    }

    private BigDecimal calculateCurrentBalance(Set<Wallet> wallets) {
        return wallets.stream()
            .map(Wallet::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}