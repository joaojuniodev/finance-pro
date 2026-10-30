package br.com.financepro.financePro.movement.controller;

import br.com.financepro.financePro.movement.controller.doc.MovementControllerDocs;
import br.com.financepro.financePro.movement.dto.MovementRequestDTO;
import br.com.financepro.financePro.movement.dto.MovementResponseDTO;
import br.com.financepro.financePro.movement.service.MovementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Movement")
@RestController
@RequestMapping("/api/movements/v1")
public class MovementController implements MovementControllerDocs {
    private final MovementService service;

    public MovementController(MovementService service) { this.service = service; }

    @GetMapping
    @Override
    public ResponseEntity<List<MovementResponseDTO>> getAll(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.getAll(principal.getName()));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<MovementResponseDTO> getById(@PathVariable UUID id, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.getById(id, principal.getName()));
    }

    @PostMapping
    @Override
    public ResponseEntity<MovementResponseDTO> create(@RequestBody MovementRequestDTO movement, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.create(movement, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        // Keep confirmed transfers and their request ids. A correction is an opposite transfer.
        return ResponseEntity.status(405).build();
    }
}
