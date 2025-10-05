package com.example.fabrick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransactionsPayloadDto {
    @NotEmpty(message = "Transaction list cannot be empty")
    @JsonProperty("list")
    private List<TransactionDto> list;
}
