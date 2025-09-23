package com.example.fabrick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaldoPayloadDto {

    @JsonProperty("availableBalance")
    private BigDecimal availableBalance;
}
