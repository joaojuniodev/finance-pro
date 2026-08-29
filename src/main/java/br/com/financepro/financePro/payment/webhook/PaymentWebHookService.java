package br.com.financepro.financePro.payment.webhook;

import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.payment.dto.request.MercadoPagoWebHookRequest;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import br.com.financepro.financePro.payment.enums.PaymentStatus;
import br.com.financepro.financePro.payment.enums.PaymentStatusDetail;
import br.com.financepro.financePro.payment.gateway.PaymentGateway;
import br.com.financepro.financePro.payment.model.Payment;
import br.com.financepro.financePro.payment.model.WebhookEvent;
import br.com.financepro.financePro.payment.repository.PaymentRepository;
import br.com.financepro.financePro.payment.repository.WebhookEventRepository;
import br.com.financepro.financePro.subscription.service.SubscriptionService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentWebHookService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WebhookEventRepository webhookEventRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private PaymentGateway paymentGateway;

    @Transactional
    public void processWebHook(MercadoPagoWebHookRequest request) {
        if (!"payment".equals(request.type())) {
            return;
        }

        String externalEventId = request.data().id();

        if (webhookEventRepository.existsByExternalEventId(externalEventId)) {
            return;
        }

        WebhookEvent webhookEvent = new WebhookEvent();

        webhookEvent.setExternalEventId(externalEventId);
        webhookEvent.setType(request.type());
        webhookEvent.setAction(request.action());
        webhookEvent.setReceivedAt(Instant.now());

        webhookEventRepository.save(webhookEvent);

        try {
            PaymentStatusResponse paymentStatus = paymentGateway.getPayment(externalEventId);

            UUID PaymentId = UUID.fromString(paymentStatus.externalReference());

            Payment payment = paymentRepository.findById(PaymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + externalEventId));

            PaymentStatus newStatus = PaymentStatus.valueOf(paymentStatus.status().toUpperCase());
            PaymentStatus currentStatus = payment.getStatus();

            payment.setExternalPaymentId(paymentStatus.paymentId());
            payment.setStatus(newStatus);
            payment.setStatusDetail(PaymentStatusDetail.valueOf(paymentStatus.statusDetail().toUpperCase()));

            if (currentStatus != PaymentStatus.APPROVED && newStatus == PaymentStatus.APPROVED) {
                payment.setPaidAt(LocalDateTime.now());
                subscriptionService.activateFromPayment(payment);
            }

            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            webhookEvent.setProcessedAt(Instant.now());
            webhookEventRepository.save(webhookEvent);
        }
        catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }
}