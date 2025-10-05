package com.example.fabrick.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TransactionDto {

    @NotBlank(message = "Transaction ID is required")
    @JsonProperty("transactionId")
    private String transactionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Accounting date is required")
    @PastOrPresent(message = "Accounting date cannot be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("accountingDate")
    private LocalDate accountingDate;
}
