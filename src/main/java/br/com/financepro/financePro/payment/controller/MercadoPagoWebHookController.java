package br.com.financepro.financePro.payment.controller;

import br.com.financepro.financePro.infrastructure.payment.mercadopago.webhook.MercadoPagoWebHookSignatureValidator;
import br.com.financepro.financePro.payment.dto.request.MercadoPagoWebHookRequest;
import br.com.financepro.financePro.payment.webhook.PaymentWebHookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/mercadopago")
public class MercadoPagoWebHookController {

    private final PaymentWebHookService paymentWebHookService;
    private final MercadoPagoWebHookSignatureValidator signatureValidator;

    public MercadoPagoWebHookController(PaymentWebHookService paymentWebHookService, MercadoPagoWebHookSignatureValidator signatureValidator) {
        this.paymentWebHookService = paymentWebHookService;
        this.signatureValidator = signatureValidator;
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebHook(
        @RequestBody MercadoPagoWebHookRequest request,
        @RequestHeader(name = "x-signature", required = false) String signature,
        @RequestHeader(name = "x-request-id", required = false) String requestId
    ) {

        String paymentId = request.data().id();

        boolean isValid = signatureValidator.isValid(signature, requestId, paymentId);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        paymentWebHookService.processWebHook(request);
        return ResponseEntity.ok().build();
    }
}