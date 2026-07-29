package br.com.financepro.financePro.payment.dto.request;

import java.math.BigDecimal;

public record CheckoutRequest(
    String title,
    String description,
    Integer quantity,
    BigDecimal unitPrice
){
}