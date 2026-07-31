package br.com.financepro.financePro.payment.service;

import br.com.financepro.financePro.payment.dto.request.CheckoutRequest;
import br.com.financepro.financePro.payment.dto.response.CheckoutResponse;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import br.com.financepro.financePro.payment.gateway.PaymentGateway;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    @Autowired
    private PaymentGateway paymentGateway;

    public CheckoutResponse createCheckout() {
        CheckoutRequest request = new CheckoutRequest(
            "FinancePro Premium",
            "Acesso ao plano FinancePro Premium",
            1,
            new BigDecimal("29.90")
        );

        try {
            return paymentGateway.createCheckout(request);
        }
        catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentStatusResponse getPayment(String paymentId) {
        try {
            return paymentGateway.getPayment(paymentId);
        }
        catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }
}