package br.com.financepro.financePro.payment.gateway;

import br.com.financepro.financePro.payment.dto.request.PaymentGatewayCheckoutRequest;
import br.com.financepro.financePro.payment.dto.response.CheckoutResponse;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface PaymentGateway {

    CheckoutResponse createCheckout(PaymentGatewayCheckoutRequest request) throws MPException, MPApiException;

    PaymentStatusResponse getPayment(String paymentId) throws MPException, MPApiException;
}