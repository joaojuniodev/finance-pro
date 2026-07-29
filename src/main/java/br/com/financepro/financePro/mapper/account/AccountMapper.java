package br.com.financepro.financePro.mapper.account;

import br.com.financepro.financePro.account.dto.request.AccountRequestDTO;
import br.com.financepro.financePro.account.dto.response.AccountResponseDTO;
import br.com.financepro.financePro.account.dto.response.BiggestExpenseResponseDTO;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.mapper.category.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountMapper implements ObjectMapper<Account, AccountResponseDTO, AccountRequestDTO> {

    @Autowired
    private CategoryMapper categoryMapper;

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
        final BigDecimal NET_INCOME = entity.getIncome().subtract(entity.getExpenses());

        CategoryResponseDTO category = entity.getBiggestExpenseCategory() != null
            ? categoryMapper.toResponse(entity.getBiggestExpenseCategory())
            : null;

        return new AccountResponseDTO(
            entity.getId(),
            entity.getCurrentBalance(),
            entity.getIncome(),
            entity.getExpenses(),
            NET_INCOME,
            new BiggestExpenseResponseDTO(
                entity.getBiggestExpenseValue(),
                category
            )
        );
    }
}