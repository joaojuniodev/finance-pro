package br.com.financepro.financePro.payment.controller;

import br.com.financepro.financePro.payment.dto.response.CheckoutResponse;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import br.com.financepro.financePro.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/v1")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createCheckout() {
        CheckoutResponse response = paymentService.createCheckout();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }
}