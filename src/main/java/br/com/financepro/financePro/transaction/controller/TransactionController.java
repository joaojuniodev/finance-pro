package br.com.financepro.financePro.transaction.controller;


import br.com.financepro.financePro.transaction.controller.doc.TransactionControllerDocs;
import br.com.financepro.financePro.transaction.dto.OverviewResponseDTO;
import br.com.financepro.financePro.transaction.dto.TransactionRequestDTO;
import br.com.financepro.financePro.transaction.dto.TransactionResponseDTO;
import br.com.financepro.financePro.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions/v1")
public class TransactionController implements TransactionControllerDocs {

    @Autowired
    private TransactionService service;

    @GetMapping
    @Override
    public ResponseEntity<List<TransactionResponseDTO>> getAll(
        @RequestParam(required = false, name = "accountId") UUID accountId,
        @RequestParam(required = false, name = "month") Integer month,
        @RequestParam(required = false, name = "year") Integer year
    ) {
        return ResponseEntity.ok().body(service.getAll(accountId, month, year));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<TransactionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @GetMapping("/overview/{accountId}")
    @Override
    public ResponseEntity<OverviewResponseDTO> overview(@PathVariable UUID accountId) {
        return ResponseEntity.ok().body(service.overview(accountId));
    }

    @PostMapping
    @Override
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody TransactionRequestDTO transaction) {
        return ResponseEntity.ok().body(service.create(transaction));
    }

    @PutMapping
    @Override
    public ResponseEntity<TransactionResponseDTO> update(@RequestBody TransactionRequestDTO transaction) {
        return ResponseEntity.ok().body(service.update(transaction));
    }

    @PatchMapping("/conclude/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> conclude(@PathVariable UUID transactionId) {
        return ResponseEntity.ok().body(service.conclude(transactionId));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}