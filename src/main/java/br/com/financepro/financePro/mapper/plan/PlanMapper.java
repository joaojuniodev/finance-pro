package br.com.financepro.financePro.mapper.plan;

import br.com.financepro.financePro.mapper.ObjectMapper;
import br.com.financepro.financePro.plan.dto.request.PlanRequestDTO;
import br.com.financepro.financePro.plan.dto.response.PlanResponseDTO;
import br.com.financepro.financePro.plan.model.Plan;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper implements ObjectMapper<Plan, PlanResponseDTO, PlanRequestDTO> {

    @Override
    public Plan toEntity(PlanRequestDTO request) {
        var plan = new Plan();
        plan.setId(request.getId());
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setType(request.getType());
        plan.setPrice(request.getPrice());
        plan.setCurrency(request.getCurrency());
        plan.setActive(request.getActive());
        plan.setCreatedAt(request.getCreatedAt());
        plan.setUpdatedAt(request.getUpdatedAt());
        return plan;
    }

    @Override
    public PlanResponseDTO toResponse(Plan entity) {
        return new PlanResponseDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getType(),
            entity.getPrice(),
            entity.getCurrency(),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}