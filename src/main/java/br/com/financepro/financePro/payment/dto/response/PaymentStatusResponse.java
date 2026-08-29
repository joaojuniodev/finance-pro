package br.com.financepro.financePro.payment.dto.response;

import java.math.BigDecimal;

public record PaymentStatusResponse(
    String paymentId,
    String status,
    String statusDetail,
    BigDecimal transactionAmount,
    String externalReference
) {
}