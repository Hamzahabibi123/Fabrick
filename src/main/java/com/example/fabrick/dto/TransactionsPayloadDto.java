package com.example.fabrick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransactionsPayloadDto {
    @JsonProperty("list")
    private List<TransactionDto> list;
}
