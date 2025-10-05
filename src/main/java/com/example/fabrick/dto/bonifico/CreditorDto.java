package com.example.fabrick.dto.bonifico;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditorDto {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Creditor account information is required")
    @Valid
    private CreditorAccountDto account;

}
