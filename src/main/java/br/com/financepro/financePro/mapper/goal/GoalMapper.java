package br.com.financepro.financePro.mapper.goal;

import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.dto.GoalResponseDTO;
import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.goal.model.Goal;
import br.com.financepro.financePro.mapper.category.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper implements ObjectMapper<Goal, GoalResponseDTO, GoalRequestDTO> {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Goal toEntity(GoalRequestDTO request) {
        return new Goal(
            request.getId(),
            request.getName(),
            request.getDescription(),
            request.getTotalAmount(),
            request.getCurrentAmount(),
            null,
            null
        );
    }

    @Override
    public GoalResponseDTO toResponse(Goal entity) {
        var category = entity.getCategory() == null ? null : categoryMapper.toResponse(entity.getCategory());
        return new GoalResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getTotalAmount(),
            entity.getCurrentAmount(),
            category
        );
    }
}
