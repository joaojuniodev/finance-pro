package br.com.financepro.financePro.invoice.filter;

import java.util.UUID;

public record InvoiceFilter(
    UUID accountId,
    UUID walletId
) {}