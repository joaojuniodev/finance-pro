package br.com.financepro.financePro.infrastructure.payment.mercadopago.gateway;

import br.com.financepro.financePro.payment.dto.request.CheckoutRequest;
import br.com.financepro.financePro.payment.dto.request.PaymentGatewayCheckoutRequest;
import br.com.financepro.financePro.payment.dto.response.CheckoutResponse;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import br.com.financepro.financePro.payment.gateway.PaymentGateway;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MercadoPagoPaymentGateway implements PaymentGateway {

    @Autowired
    private PreferenceClient preferenceClient;

    @Autowired
    private PaymentClient paymentClient;

    @Override
    public CheckoutResponse createCheckout(PaymentGatewayCheckoutRequest request) throws MPException, MPApiException {
        PreferenceItemRequest item = PreferenceItemRequest.builder()
            .title(request.title())
            .description(request.description())
            .quantity(request.quantity())
            .unitPrice(request.unitPrice())
            .currencyId("BRL")
            .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(List.of(item))
            .externalReference(request.externalReference())
            .build();

        Preference preference = preferenceClient.create(preferenceRequest);

        return new CheckoutResponse(preference.getId(), preference.getInitPoint());
    }

    @Override
    public PaymentStatusResponse getPayment(String paymentId) throws MPException, MPApiException {
        Payment payment = paymentClient.get(Long.valueOf(paymentId));
        return new PaymentStatusResponse(
            String.valueOf(payment.getId()),
            payment.getStatus(),
            payment.getStatusDetail(),
            payment.getTransactionAmount(),
            payment.getExternalReference()
        );
    }
}