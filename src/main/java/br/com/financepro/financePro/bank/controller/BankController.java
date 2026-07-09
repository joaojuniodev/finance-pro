package br.com.financepro.financePro.bank.controller;

import br.com.financepro.financePro.bank.controller.doc.BankControllerDocs;
import br.com.financepro.financePro.bank.dto.BankRequestDTO;
import br.com.financepro.financePro.bank.dto.BankResponseDTO;
import br.com.financepro.financePro.bank.service.BankService;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Bank")
@RestController
@RequestMapping("/api/banks/v1")
public class BankController implements BankControllerDocs {

    @Autowired
    private BankService service;

    @GetMapping
    @Override
    public ResponseEntity<List<BankResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<BankResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<BankResponseDTO> create(@RequestBody BankRequestDTO bank) {
        return ResponseEntity.ok().body(service.create(bank));
    }

    @PutMapping
    @Override
    public ResponseEntity<BankResponseDTO> update(@RequestBody BankRequestDTO bank) {
        return ResponseEntity.ok().body(service.update(bank));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}