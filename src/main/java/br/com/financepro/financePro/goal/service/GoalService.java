package br.com.financepro.financePro.goal.service;

import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.dto.GoalResponseDTO;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.mapper.goal.GoalMapper;
import br.com.financepro.financePro.goal.repository.GoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final Logger log = LoggerFactory.getLogger(GoalService.class.getName());

    @Autowired
    private GoalRepository repository;

    @Autowired
    private GoalMapper mapper;

    public List<GoalResponseDTO> getAll() {
        log.info("Getting All Goals");

        return repository.findAll()
            .stream()
            .map(entity -> mapper.toResponse(entity))
            .toList();
    }

    public GoalResponseDTO getById(UUID id) {
        log.info("Getting Goal by Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        return mapper.toResponse(entity);
    }

    public GoalResponseDTO create(GoalRequestDTO goal) {
        log.info("Creating Goal");

        var goalCreated = repository.save(mapper.toEntity(goal));
        return mapper.toResponse(goalCreated);
    }

    public GoalResponseDTO update(GoalRequestDTO goal) {
        log.info("Updating Goal");

        var entity = repository.findById(goal.getId())
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + goal.getId()));
        entity.setName(goal.getName());
        entity.setDescription(goal.getDescription());
        entity.setTotalAmount(goal.getTotalAmount());
        entity.setCurrentAmount(goal.getCurrentAmount());

        var goalUpdated = repository.save(entity);
        return mapper.toResponse(goalUpdated);
    }

    public GoalResponseDTO incrementAmount(UUID id, BigDecimal amount) {
        log.info("Increment amount by Goal Id");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        entity.setCurrentAmount(entity.getCurrentAmount().add(amount));

        var goalUpdated = repository.save(entity);
        return mapper.toResponse(goalUpdated);
    }

    public void delete(UUID id) {
        log.info("Deleting Goal");

        var entity = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Not found this Id: " + id));
        repository.delete(entity);
    }
}