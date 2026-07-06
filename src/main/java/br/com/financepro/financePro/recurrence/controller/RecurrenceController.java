package br.com.financepro.financePro.recurrence.controller;

import br.com.financepro.financePro.recurrence.controller.doc.RecurrenceControllerDocs;
import br.com.financepro.financePro.recurrence.dto.RecurrenceRequestDTO;
import br.com.financepro.financePro.recurrence.dto.RecurrenceResponseDTO;
import br.com.financepro.financePro.recurrence.service.RecurrenceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.apache.bcel.classfile.Module;
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
    private RecurrenceService service;

    @GetMapping
    @Override
    public ResponseEntity<List<RecurrenceResponseDTO>> getAll(
        @RequestParam(required = false, name = "accountId") UUID accountId
    ) {
        return ResponseEntity.ok().body(service.getAll(accountId));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<RecurrenceResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<RecurrenceResponseDTO> create(@RequestBody RecurrenceRequestDTO recurrence) {
        return ResponseEntity.ok().body(service.create(recurrence));
    }

    @PutMapping
    @Override
    public ResponseEntity<RecurrenceResponseDTO> update(@RequestBody RecurrenceRequestDTO recurrence) {
        return ResponseEntity.ok().body(service.update(recurrence));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}