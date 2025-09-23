package com.example.fabrick.dto.bonifico;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BonificoRequestDto {

    private CreditorDto creditor;
    private String description;
    private String currency;
    private String amount;
    private String executionDate;
}
