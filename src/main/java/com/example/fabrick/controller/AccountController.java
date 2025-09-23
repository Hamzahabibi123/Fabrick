package com.example.fabrick.controller;

import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.service.FabrickService;
import com.example.fabrick.utli.Constants;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final FabrickService fabrickService;

    public AccountController(FabrickService fabrickService) {
        this.fabrickService = fabrickService;
    }

    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long accountId) {
        if (!accountId.equals(Constants.ACCOUNT_ID)) {
            return ResponseEntity.badRequest().body("Invalid accountId");
        }
        BigDecimal balance = fabrickService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getTransactions(
            @PathVariable Long accountId,
            @RequestParam String fromAccountingDate,
            @RequestParam String toAccountingDate) {
        if (!accountId.equals(Constants.ACCOUNT_ID)) {
            return ResponseEntity.badRequest().body("Invalid accountId");
        }
        TransactionResponseDto resp = fabrickService.getTransactions(accountId, fromAccountingDate, toAccountingDate);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/accounts/{accountId}/payments/money-transfers")
    public ResponseEntity<?> bonifico(@PathVariable Long accountId,
                                           @Valid @RequestBody BonificoRequestDto req) {
        if (!accountId.equals(Constants.ACCOUNT_ID)) {
            return ResponseEntity.badRequest().body("Invalid accountId");
        }
        BonificoResponseDto resp = fabrickService.makeMoneyTransfer(accountId, req);
        return ResponseEntity.ok(resp);
    }

}
