package br.com.financepro.financePro.invoice.controller;

import br.com.financepro.financePro.invoice.dto.CreditCardInvoiceRequestDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceDetailsResponseDTO;
import br.com.financepro.financePro.invoice.service.CreditCardInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.security.Principal;

@RestController
@RequestMapping("/api/invoices/v1")
public class InvoiceController {

    @Autowired
    private CreditCardInvoiceService creditCardInvoiceService;

    @GetMapping("/{id}/credit-card-bill")
    public ResponseEntity<InvoiceDetailsResponseDTO> getCreditCardBill(@PathVariable UUID id, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(creditCardInvoiceService.getInvoiceDetails(id, principal.getName()));
    }

    @PatchMapping("/{id}/credit-card-bill/installments/{installmentId}")
    public ResponseEntity<InvoiceDetailsResponseDTO> payInstallment(
        @PathVariable UUID id,
        @PathVariable UUID installmentId,
        @RequestBody CreditCardInvoiceRequestDTO payment, Principal principal
    ) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(creditCardInvoiceService.payInstallment(id, installmentId, payment.paymentWalletId(), principal.getName()));
    }

    @PatchMapping("/{id}/credit-card-bill")
    public ResponseEntity<InvoiceDetailsResponseDTO> payCreditCardBill(
        @PathVariable UUID id,
        @RequestBody CreditCardInvoiceRequestDTO payment, Principal principal
    ) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(creditCardInvoiceService.payBill(id, payment.paymentWalletId(), principal.getName()));
    }
}
