package br.com.financepro.financePro.plan.service;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.plan.PlanMapper;
import br.com.financepro.financePro.plan.dto.request.PlanRequestDTO;
import br.com.financepro.financePro.plan.dto.response.PlanResponseDTO;
import br.com.financepro.financePro.plan.repository.PlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlanService {

    private final Logger log = LoggerFactory.getLogger(PlanService.class.getName());

    @Autowired
    private PlanRepository repository;

    @Autowired
    private PlanMapper mapper;

    public List<PlanResponseDTO> getAll() {
        log.info("Getting all Plans");

        return repository.findAll().stream()
            .map(subscription -> mapper.toResponse(subscription))
            .toList();
    }

    public PlanResponseDTO getById(UUID id) {
        log.info("Getting by Id Plan");

        var subscription = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this ID: " + id));
        return mapper.toResponse(subscription);
    }

    public PlanResponseDTO create(PlanRequestDTO plan) {
        log.info("Creating Plan");

        var planCreated = repository.save(mapper.toEntity(plan));
        return mapper.toResponse(planCreated);
    }

    public PlanResponseDTO update(PlanRequestDTO plan) {
        log.info("Updating Plan");

        var entity = repository.findById(plan.getId())
            .orElseThrow(() -> new NotFoundException("Not found this ID: " + plan.getId()));
        entity.setName(plan.getName());
        entity.setDescription(plan.getDescription());
        var planUpdated = repository.save(entity);
        return mapper.toResponse(planUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Plan");

        var plan = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this ID: " + id));
        repository.delete(plan);
    }
}