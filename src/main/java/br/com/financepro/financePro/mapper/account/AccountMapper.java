package br.com.financepro.financePro.mapper.account;

import br.com.financepro.financePro.data.dtos.request.account.AccountRequestDTO;
import br.com.financepro.financePro.data.dtos.response.account.AccountResponseDTO;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.models.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper implements ObjectMapper<Account, AccountResponseDTO, AccountRequestDTO> {

    @Override
    public Account toEntity(AccountRequestDTO dto) {
        return new Account(
            dto.getId(),
            dto.getCurrentBalance(),
            dto.getIncome(),
            dto.getExpenses(),
            dto.getNetIncome(),
            dto.getBiggestExpense()
        );
    }

    @Override
    public AccountResponseDTO toResponse(Account entity) {
        return new AccountResponseDTO(
            entity.getId(),
            entity.getCurrentBalance(),
            entity.getIncome(),
            entity.getExpenses(),
            entity.getNetIncome(),
            entity.getBiggestExpense()
        );
    }
}