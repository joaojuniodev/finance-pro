package br.com.financepro.financePro.payment.service;

import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.common.exceptions.NotFoundException;
import br.com.financepro.financePro.payment.dto.request.CheckoutRequest;
import br.com.financepro.financePro.payment.dto.request.PaymentGatewayCheckoutRequest;
import br.com.financepro.financePro.payment.dto.response.CheckoutResponse;
import br.com.financepro.financePro.payment.dto.response.PaymentStatusResponse;
import br.com.financepro.financePro.payment.enums.PaymentStatus;
import br.com.financepro.financePro.payment.gateway.PaymentGateway;
import br.com.financepro.financePro.payment.model.Payment;
import br.com.financepro.financePro.payment.repository.PaymentRepository;
import br.com.financepro.financePro.plan.repository.PlanRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentCheckoutService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentGateway paymentGateway;

    public CheckoutResponse createCheckout(CheckoutRequest request) {
        var plan = planRepository.findById(request.planId())
            .orElseThrow(() -> new NotFoundException("Not found Plan Id: " + request.planId()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var account = accountRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new NotFoundException("Not found Account Id: " + request.planId()));

        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(plan.getPrice());
        payment.setCurrency(plan.getCurrency());
        payment.setPlan(plan);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setAccount(account);

        paymentRepository.save(payment);

        PaymentGatewayCheckoutRequest gatewayRequest = new PaymentGatewayCheckoutRequest(
            plan.getName(),
            plan.getDescription(),
            1,
            plan.getPrice(),
            payment.getId().toString()
        );

        try {
            CheckoutResponse checkout = paymentGateway.createCheckout(gatewayRequest);

            payment.setExternalPreferenceId(checkout.preferenceId());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            return checkout;
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