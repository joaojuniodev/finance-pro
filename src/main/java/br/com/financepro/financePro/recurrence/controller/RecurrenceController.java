package br.com.financepro.financePro.recurrence.controller;

import br.com.financepro.financePro.recurrence.controller.doc.RecurrenceControllerDocs;
import br.com.financepro.financePro.recurrence.dto.response.AllRecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.dto.request.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceConfirmResponseDTO;
import br.com.financepro.financePro.recurrence.dto.response.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.service.RecurrenceReadService;
import br.com.financepro.financePro.recurrence.service.RecurrenceSaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import java.security.Principal;

import java.util.List;
import java.util.UUID;

@Tag(name = "Recurrence")
@RestController
@RequestMapping("/api/recurrences/v1")
public class RecurrenceController implements RecurrenceControllerDocs {

    @Autowired
    private RecurrenceReadService readService;

    @Autowired
    private RecurrenceSaveService saveService;

    @Autowired
    private AccountRepository accounts;

    @GetMapping("/by-account/{accountId}")
    @Override
    public ResponseEntity<List<RecurrenceResponseDTO>> getAll(@PathVariable UUID accountId, Principal principal) {
        requireAccount(accountId, principal);
        return ResponseEntity.ok().body(readService.getAll(accountId));
    }

    @GetMapping("/overview/{accountId}")
    @Override
    public ResponseEntity<AllRecurrenceResponseDTO> getOverview(@PathVariable UUID accountId, Principal principal) {
        requireAccount(accountId, principal);
        return ResponseEntity.ok().body(readService.getOverview(accountId));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<RecurrenceResponseDTO> getById(@PathVariable UUID id, Principal principal) {
        return ResponseEntity.ok().body(readService.getById(id, principal.getName()));
    }

    @PostMapping
    @Override
    public ResponseEntity<RecurrenceResponseDTO> create(@RequestBody RecurrenceRequestDTO recurrence, Principal principal) {
        requireAccount(recurrence.getAccountId(), principal);
        return ResponseEntity.ok().body(saveService.create(recurrence));
    }

    @PutMapping
    @Override
    public ResponseEntity<RecurrenceResponseDTO> update(@RequestBody RecurrenceRequestDTO recurrence, Principal principal) {
        requireAccount(recurrence.getAccountId(), principal);
        return ResponseEntity.ok().body(saveService.update(recurrence, principal.getName()));
    }

    @PatchMapping("/confirm/{recurrenceId}")
    @Override
    public ResponseEntity<?> confirm(@PathVariable UUID recurrenceId, Principal principal) {
        saveService.confirm(recurrenceId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/pause/{recurrenceId}")
    public ResponseEntity<?> pause(@PathVariable UUID recurrenceId, Principal principal) {
        saveService.pause(recurrenceId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate/{recurrenceId}")
    public ResponseEntity<?> activate(@PathVariable UUID recurrenceId, Principal principal) {
        saveService.activate(recurrenceId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/finish/{recurrenceId}")
    public ResponseEntity<?> finish(@PathVariable UUID recurrenceId, Principal principal) {
        saveService.finish(recurrenceId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id, java.security.Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        saveService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    private void requireAccount(UUID accountId, Principal principal) {
        if (principal == null || accounts.findByUsername(principal.getName())
            .filter(account -> account.getId().equals(accountId)).isEmpty()) {
            throw new NotFoundException("Conta não encontrada.");
        }
    }
}
