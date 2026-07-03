package br.com.financepro.financePro.movement.controller;


import br.com.financepro.financePro.movement.controller.doc.MovementControllerDocs;
import br.com.financepro.financePro.movement.dto.MovementRequestDTO;
import br.com.financepro.financePro.movement.dto.MovementResponseDTO;
import br.com.financepro.financePro.movement.service.MovementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Movement")
@RestController
@RequestMapping("/api/movements/v1")
public class MovementController implements MovementControllerDocs {

    @Autowired
    private MovementService service;

    @GetMapping
    @Override
    public ResponseEntity<List<MovementResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<MovementResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<MovementResponseDTO> create(@RequestBody MovementRequestDTO movement) {
        return ResponseEntity.ok().body(service.create(movement));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}