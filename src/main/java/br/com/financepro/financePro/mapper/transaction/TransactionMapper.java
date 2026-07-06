package br.com.financepro.financePro.mapper.transaction;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.transaction.dto.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.TransactionResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.account.model.Account;
import br.com.financepro.financePro.transaction.model.Transaction;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements ObjectMapper<Transaction, TransactionResponseDTO, TransactionRequestDTO> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Transaction toEntity(TransactionRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new NotFoundException("Not found this Account Id: " + request.getAccountId()));
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + request.getCategoryId()));
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this Category Id: " + request.getCategoryId()));
        return new Transaction(
            request.getId(),
            request.getAmount(),
            request.getDescription(),
            request.getObservation(),
            request.getType(),
            request.getStatus(),
            request.getRegisteredAt(),
            category,
            wallet,
            account
        );
    }

    @Override
    public TransactionResponseDTO toResponse(Transaction entity) {
        CategoryResponseDTO category = entity.getCategory() != null
            ? new CategoryResponseDTO(
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getType(),
                entity.getCategory().getIcon(),
                entity.getCategory().getSystem())
            : null;
        return new TransactionResponseDTO(
            entity.getId(),
            entity.getAmount(),
            entity.getDescription(),
            entity.getObservation(),
            entity.getType(),
            entity.getStatus(),
            category,
            entity.getRegisteredAt(),
            entity.getRecurrence() != null
                ? entity.getRecurrence().getId()
                : null
        );
    }
}