package br.com.financepro.financePro.payment.dto.request;

import java.math.BigDecimal;

public record PaymentGatewayCheckoutRequest(
    String title,
    String description,
    Integer quantity,
    BigDecimal unitPrice,
    String externalReference
) {
}