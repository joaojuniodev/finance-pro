package br.com.financepro.financePro.wallet.controller;

import br.com.financepro.financePro.wallet.controller.doc.WalletControllerDocs;
import br.com.financepro.financePro.wallet.dto.WalletSummaryDTO;
import br.com.financepro.financePro.wallet.dto.WalletRequestDTO;
import br.com.financepro.financePro.wallet.dto.WalletResponseDTO;
import br.com.financepro.financePro.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Wallet")
@RestController
@RequestMapping("/api/wallets/v1")
public class WalletController implements WalletControllerDocs {

    @Autowired
    private WalletService service;

    @GetMapping
    @Override
    public ResponseEntity<List<WalletResponseDTO>> getAll(
        @RequestParam(required = false, name = "accountId") UUID accountId
    ) {
        return ResponseEntity.ok().body(service.getAll(accountId));
    }

    @GetMapping("/summary/{accountId}/{walletId}")
    @Override
    public ResponseEntity<WalletSummaryDTO> getSummary(@PathVariable UUID accountId, @PathVariable UUID walletId) {
        return ResponseEntity.ok().body(service.summary(accountId, walletId));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<WalletResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    @Override
    public ResponseEntity<WalletResponseDTO> create(@RequestBody WalletRequestDTO wallet) {
        return ResponseEntity.ok().body(service.create(wallet));
    }

    @PutMapping
    @Override
    public ResponseEntity<WalletResponseDTO> update(@RequestBody WalletRequestDTO wallet) {
        return ResponseEntity.ok().body(service.update(wallet));
    }

    @PatchMapping("/{id}/{amount}")
    @Override
    public ResponseEntity<WalletResponseDTO> incrementAmount(
        @PathVariable UUID id,
        @PathVariable BigDecimal amount
    ) {
        return ResponseEntity.ok().body(service.incrementAmount(id, amount));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}