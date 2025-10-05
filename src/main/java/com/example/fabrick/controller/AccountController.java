package com.example.fabrick.controller;

import com.example.fabrick.configurazione.FabrickProperties;
import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.service.FabrickService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final FabrickService fabrickService;
    private final FabrickProperties fabrickProperties;

    public AccountController(FabrickService fabrickService, FabrickProperties fabrickProperties) {
        this.fabrickService = fabrickService;
        this.fabrickProperties = fabrickProperties;
    }

    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long accountId) {
        if (!accountId.equals(fabrickProperties.getAccountId())) {
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
        if (!accountId.equals(fabrickProperties.getAccountId())) {
            return ResponseEntity.badRequest().body("Invalid accountId");
        }
        TransactionResponseDto resp = fabrickService.getTransactions(accountId, fromAccountingDate, toAccountingDate);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/accounts/{accountId}/payments/money-transfers")
    public ResponseEntity<?> bonifico(@PathVariable Long accountId,
                                           @Valid @RequestBody BonificoRequestDto req) {
        if (!accountId.equals(fabrickProperties.getAccountId())) {
            return ResponseEntity.badRequest().body("Invalid accountId");
        }
        BonificoResponseDto resp = fabrickService.makeMoneyTransfer(accountId, req);
        return ResponseEntity.ok(resp);
    }

}
