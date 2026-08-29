package br.com.financepro.financePro.account.service.shared;

import br.com.financepro.financePro.transaction.dto.projection.CategoryAmountProjection;
import br.com.financepro.financePro.transaction.dto.response.CategorySpendingDTO;
import br.com.financepro.financePro.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class AccountSpendingCategoriesService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<CategorySpendingDTO> getTopSpendingCategories(UUID accountId, int limit) {
        List<CategoryAmountProjection> results = transactionRepository.findSpendingByCategory(accountId);

        BigDecimal total = results.stream()
            .map(CategoryAmountProjection::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
            .map(r -> {
                double percentage = total.compareTo(BigDecimal.ZERO) == 0
                    ? 0.0
                    : r.getTotalAmount()
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();

                return new CategorySpendingDTO(
                    UUID.fromString(r.getId()),
                    r.getName(),
                    r.getColor(),
                    r.getIcon(),
                    r.getTotalAmount(),
                    percentage
                );
            })
            .sorted(Comparator.comparing(CategorySpendingDTO::amount).reversed())
            .limit(limit)
            .toList();
    }

    public CategorySpendingDTO getTopSpendingCategory(UUID accountId) {
        Object[] row = transactionRepository.findTopSpendingCategoryNative(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Nenhum gasto encontrado para este usuário"));

        return new CategorySpendingDTO(
            (UUID) row[0],
            (String) row[1],
            (String) row[2],
            (String) row[3],
            (BigDecimal) row[4],
            ((Number) row[5]).doubleValue()
        );
    }
}