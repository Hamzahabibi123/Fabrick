package com.example.fabrick.dto.bonifico;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditorAccountDto {
    @NotNull(message = "Name is required")
    private String accountCode;
}
