package br.com.financepro.financePro.payment.webhook;

import br.com.financepro.financePro.payment.dto.request.MercadoPagoWebHookRequest;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import br.com.financepro.financePro.payment.gateway.PaymentGateway;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.stereotype.Service;

@Service
public class PaymentWebHookService {

    private final PaymentGateway paymentGateway;

    public PaymentWebHookService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public void processWebHook(MercadoPagoWebHookRequest request) {
        if (!"payment".equals(request.type())) {
            return;
        }

        String paymentId = request.data().id();

        try {
            PaymentStatusResponse payment = paymentGateway.getPayment(paymentId);

            System.out.println("========== PAGAMENTO CONSULTADO ==========");
            System.out.println("Payment ID: " + payment.paymentId());
            System.out.println("Status: " + payment.status());
            System.out.println("Status Detail: " + payment.statusDetail());
            System.out.println("Valor: " + payment.transactionAmount());
        }
        catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }
}