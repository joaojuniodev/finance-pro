package br.com.financepro.financePro.mapper.goal;

import br.com.financepro.financePro.category.dto.CategoryResponseDTO;
import br.com.financepro.financePro.category.model.Category;
import br.com.financepro.financePro.category.repository.CategoryRepository;
import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.dto.GoalResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.goal.model.Goal;
import br.com.financepro.financePro.wallet.model.Wallet;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper implements ObjectMapper<Goal, GoalResponseDTO, GoalRequestDTO> {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Goal toEntity(GoalRequestDTO request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Not found this Wallet Id: " + request.getWalletId()));
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Not found this Wallet Id: " + request.getCategoryId()));
        return new Goal(
            request.getId(),
            request.getName(),
            request.getDescription(),
            request.getTotalAmount(),
            request.getCurrentAmount(),
            category,
            wallet
        );
    }

    @Override
    public GoalResponseDTO toResponse(Goal entity) {
        return new GoalResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getTotalAmount(),
            entity.getCurrentAmount(),
            new CategoryResponseDTO(
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getType(),
                entity.getCategory().getIcon(),
                entity.getCategory().getSystem()
            )
        );
    }
}
