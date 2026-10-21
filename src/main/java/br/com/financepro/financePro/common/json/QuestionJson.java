package br.com.financepro.financePro.common.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record QuestionJson(
    String id,

    @JsonProperty("title")
    String title,

    Map<String, Integer> options
){}