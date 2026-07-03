package br.com.financepro.financePro.mapper.category;

import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.category.dto.CategoryRequestDTO;
import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements ObjectMapper<Category, CategoryResponseDTO, CategoryRequestDTO> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Category toEntity(CategoryRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        return new Category(
            request.getId(),
            request.getName(),
            request.getType(),
            request.getIcon(),
            request.getSystem(),
            account
        );
    }

    @Override
    public CategoryResponseDTO toResponse(Category entity) {
        return new CategoryResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getType(),
            entity.getIcon(),
            entity.getSystem()
        );
    }
}