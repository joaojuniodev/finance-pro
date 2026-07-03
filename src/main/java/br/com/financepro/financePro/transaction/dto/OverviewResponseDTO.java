package br.com.financepro.financePro.transaction.dto;

import java.util.List;
import java.util.Map;

public record OverviewResponseDTO(
    Map<String, List<WeekOverviewResponse>> overview
) {
}