package br.com.financepro.financePro.mapper.bank;

import br.com.financepro.financePro.bank.dto.BankRequestDTO;
import br.com.financepro.financePro.bank.dto.BankResponseDTO;
import br.com.financepro.financePro.bank.model.Bank;
import br.com.financepro.financePro.mapper.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class BankMapper implements ObjectMapper<Bank, BankResponseDTO, BankRequestDTO> {

    @Override
    public Bank toEntity(BankRequestDTO request) {
        return new Bank(
            request.getId(),
            request.getName(),
            request.getIcon(),
            request.getColor(),
            request.getGradient(),
            request.getShadow()
        );
    }

    @Override
    public BankResponseDTO toResponse(Bank entity) {
        return new BankResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getIcon(),
            entity.getColor(),
            entity.getGradient(),
            entity.getShadow()
        );
    }
}