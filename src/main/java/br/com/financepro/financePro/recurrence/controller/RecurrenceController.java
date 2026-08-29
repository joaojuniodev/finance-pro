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

    @GetMapping("/{accountId}")
    @Override
    public ResponseEntity<List<RecurrenceResponseDTO>> getAll(@PathVariable UUID accountId) {
        return ResponseEntity.ok().body(readService.getAll(accountId));
    }

    @GetMapping("/overview/{accountId}")
    @Override
    public ResponseEntity<AllRecurrenceResponseDTO> getOverview(@PathVariable UUID accountId) {
        return ResponseEntity.ok().body(readService.getOverview(accountId));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<RecurrenceResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(readService.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<RecurrenceResponseDTO> create(@RequestBody RecurrenceRequestDTO recurrence) {
        return ResponseEntity.ok().body(saveService.create(recurrence));
    }

    @PutMapping
    @Override
    public ResponseEntity<RecurrenceResponseDTO> update(@RequestBody RecurrenceRequestDTO recurrence) {
        return ResponseEntity.ok().body(saveService.update(recurrence));
    }

    @PatchMapping("/confirm/{recurrenceId}")
    @Override
    public ResponseEntity<?> confirm(@PathVariable UUID recurrenceId) {
        saveService.confirm(recurrenceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/pause/{recurrenceId}")
    public ResponseEntity<?> pause(@PathVariable UUID recurrenceId) {
        saveService.pause(recurrenceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate/{recurrenceId}")
    public ResponseEntity<?> activate(@PathVariable UUID recurrenceId) {
        saveService.activate(recurrenceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/finish/{recurrenceId}")
    public ResponseEntity<?> finish(@PathVariable UUID recurrenceId) {
        saveService.finish(recurrenceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        saveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}