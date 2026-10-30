package br.com.financepro.financePro.goal.controller;

import br.com.financepro.financePro.goal.controller.doc.GoalControllerDocs;
import br.com.financepro.financePro.goal.dto.GoalRequestDTO;
import br.com.financepro.financePro.goal.dto.GoalResponseDTO;
import br.com.financepro.financePro.goal.service.GoalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Goal")
@RestController
@RequestMapping("/api/goals/v1")
public class GoalController implements GoalControllerDocs {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<GoalResponseDTO>> getAll(Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.getAll(principal.getName()));
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<GoalResponseDTO>> getByAccount(@PathVariable UUID accountId, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.getByAccount(accountId, principal.getName()));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<GoalResponseDTO> getById(@PathVariable UUID id, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.getById(id, principal.getName()));
    }

    @PostMapping
    @Override
    public ResponseEntity<GoalResponseDTO> create(@RequestBody GoalRequestDTO goal, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.create(goal, principal.getName()));
    }

    @PutMapping
    @Override
    public ResponseEntity<GoalResponseDTO> update(@RequestBody GoalRequestDTO goal, Principal principal) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.update(goal, principal.getName()));
    }

    @PatchMapping("/{id}/{amount}")
    @Override
    public ResponseEntity<GoalResponseDTO> incrementAmount(
        @PathVariable UUID id,
        @PathVariable BigDecimal amount,
        Principal principal
    ) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
            .body(service.incrementAmount(id, amount, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id, Principal principal) {
        service.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
