package br.com.financepro.financePro.goal.controller;


import br.com.financepro.financePro.goal.controller.doc.GoalControllerDocs;
import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.dto.GoalResponseDTO;
import br.com.financepro.financePro.goal.service.GoalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Goal")
@RestController
@RequestMapping("/api/goals/v1")
public class GoalController implements GoalControllerDocs {

    @Autowired
    private GoalService service;

    @GetMapping
    @Override
    public ResponseEntity<List<GoalResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<GoalResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<GoalResponseDTO> create(@RequestBody GoalRequestDTO goal) {
        return ResponseEntity.ok().body(service.create(goal));
    }

    @PutMapping
    @Override
    public ResponseEntity<GoalResponseDTO> update(@RequestBody GoalRequestDTO goal) {
        return ResponseEntity.ok().body(service.update(goal));
    }

    @PatchMapping("/{id}/{amount}")
    @Override
    public ResponseEntity<GoalResponseDTO> incrementAmount(
        @PathVariable UUID id,
        @PathVariable BigDecimal amount
    ) {
        return ResponseEntity.ok().body(service.incrementAmount(id, amount));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}