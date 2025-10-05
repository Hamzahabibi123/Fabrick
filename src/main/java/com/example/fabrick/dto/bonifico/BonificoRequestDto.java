package com.example.fabrick.dto.bonifico;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BonificoRequestDto {
    @NotNull(message = "Creditor information is required")
    @Valid
    private CreditorDto creditor;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp =  "^EUR$", message = "Currency must be one of: EUR, USD, GBP")
    private String currency;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private String amount;

    @NotNull(message = "Execution date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String executionDate;
}
