package br.com.financepro.financePro.payment.dto.request;

public record MercadoPagoWebHookRequest(
    String action,
    String type,
    MercadoPagoWebHookData data
) {
}