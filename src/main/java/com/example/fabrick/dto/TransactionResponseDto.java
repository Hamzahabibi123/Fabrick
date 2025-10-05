package com.example.fabrick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransactionResponseDto {

    @JsonProperty("payload")
    private TransactionsPayloadDto payload;

}
