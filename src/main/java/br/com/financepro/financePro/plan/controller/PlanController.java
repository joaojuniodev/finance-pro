package br.com.financepro.financePro.plan.controller;

import br.com.financepro.financePro.plan.dto.request.PlanRequestDTO;
import br.com.financepro.financePro.plan.dto.response.PlanResponseDTO;
import br.com.financepro.financePro.plan.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans/v1")
public class PlanController {

    @Autowired
    private PlanService service;

    @GetMapping
    public ResponseEntity<List<PlanResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<PlanResponseDTO> create(@RequestBody PlanRequestDTO plan) {
        return ResponseEntity.ok().body(service.create(plan));
    }

    @PutMapping
    public ResponseEntity<PlanResponseDTO> update(@RequestBody PlanRequestDTO plan) {
        return ResponseEntity.ok().body(service.update(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}