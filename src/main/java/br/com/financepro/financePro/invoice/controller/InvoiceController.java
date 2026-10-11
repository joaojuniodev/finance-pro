package br.com.financepro.financePro.invoice.controller;

import br.com.financepro.financePro.invoice.dto.CreditCardInvoiceRequestDTO;
import br.com.financepro.financePro.invoice.dto.InvoiceDetailsResponseDTO;
import br.com.financepro.financePro.invoice.service.CreditCardInvoiceService;
import br.com.financepro.financePro.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoices/v1")
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @Autowired
    private CreditCardInvoiceService creditCardInvoiceService;

    @GetMapping("/{id}/credit-card-bill")
    public ResponseEntity<InvoiceDetailsResponseDTO> getCreditCardBill(@PathVariable UUID id) {
        return ResponseEntity.ok(creditCardInvoiceService.getInvoiceDetails(id));
    }

    @PatchMapping("/{id}/credit-card-bill/installments/{installmentId}")
    public ResponseEntity<InvoiceDetailsResponseDTO> payInstallment(
        @PathVariable UUID id,
        @PathVariable UUID installmentId,
        @RequestBody CreditCardInvoiceRequestDTO payment
    ) {
        return ResponseEntity.ok(creditCardInvoiceService.payInstallment(id, installmentId, payment.paymentWalletId()));
    }

    @PatchMapping("/{id}/credit-card-bill")
    public ResponseEntity<InvoiceDetailsResponseDTO> payCreditCardBill(
        @PathVariable UUID id,
        @RequestBody CreditCardInvoiceRequestDTO payment
    ) {
        return ResponseEntity.ok(creditCardInvoiceService.payBill(id, payment.paymentWalletId()));
    }
}