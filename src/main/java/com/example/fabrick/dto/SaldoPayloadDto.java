package com.example.fabrick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaldoPayloadDto {
    @NotNull(message = "Available balance cannot be null")
    @JsonProperty("availableBalance")
    private BigDecimal availableBalance;
}
