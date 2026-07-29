package br.com.financepro.financePro.recurrence.controller;

import br.com.financepro.financePro.recurrence.controller.doc.RecurrenceControllerDocs;
import br.com.financepro.financePro.recurrence.dto.AllRecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.service.RecurrenceReadService;
import br.com.financepro.financePro.recurrence.service.RecurrenceSaveService;
import br.com.financepro.financePro.recurrence.service.params.RecurrenceSearchParams;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    @Override
    public ResponseEntity<List<RecurrenceResponseDTO>> getAll(@ModelAttribute RecurrenceSearchParams params) {
        return ResponseEntity.ok().body(readService.getAll(params));
    }

    @GetMapping("/overview/{id}")
    @Override
    public ResponseEntity<AllRecurrenceResponseDTO> getOverview(@PathVariable UUID id) {
        return ResponseEntity.ok().body(readService.getOverview(id));
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

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        saveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}