package com.example.fabrick.controller;

import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.service.FabrickService;
import com.example.fabrick.utli.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    private FabrickService fabrickService;
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        fabrickService = mock(FabrickService.class);
        accountController = new AccountController(fabrickService);
    }

    @Test
    void testGetBalance_success() {
        BigDecimal expectedBalance = BigDecimal.valueOf(1234.56);
        when(fabrickService.getBalance(Constants.ACCOUNT_ID)).thenReturn(expectedBalance);

        ResponseEntity<?> response = accountController.getBalance(Constants.ACCOUNT_ID);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedBalance, response.getBody());
        verify(fabrickService, times(1)).getBalance(Constants.ACCOUNT_ID);
    }

    @Test
    void testGetBalance_invalidAccount() {
        ResponseEntity<?> response = accountController.getBalance(999L);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid accountId", response.getBody());
        verifyNoInteractions(fabrickService);
    }

    @Test
    void testGetTransactions_success() {
        TransactionResponseDto transactions = new TransactionResponseDto();
        when(fabrickService.getTransactions(Constants.ACCOUNT_ID, "2025-01-01", "2025-01-31"))
                .thenReturn(transactions);

        ResponseEntity<?> response = accountController.getTransactions(
                Constants.ACCOUNT_ID, "2025-01-01", "2025-01-31");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
        verify(fabrickService, times(1)).getTransactions(Constants.ACCOUNT_ID, "2025-01-01", "2025-01-31");
    }

    @Test
    void testGetTransactions_invalidAccount() {
        ResponseEntity<?> response = accountController.getTransactions(999L, "2025-01-01", "2025-01-31");
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid accountId", response.getBody());
        verifyNoInteractions(fabrickService);
    }

    @Test
    void testBonifico_success() {
        BonificoRequestDto request = new BonificoRequestDto();
        BonificoResponseDto responseDto = new BonificoResponseDto("OK", null, "Transfer accepted");

        when(fabrickService.makeMoneyTransfer(Constants.ACCOUNT_ID, request)).thenReturn(responseDto);

        ResponseEntity<?> response = accountController.bonifico(Constants.ACCOUNT_ID, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
        verify(fabrickService, times(1)).makeMoneyTransfer(Constants.ACCOUNT_ID, request);
    }

    @Test
    void testBonifico_invalidAccount() {
        BonificoRequestDto request = new BonificoRequestDto();

        ResponseEntity<?> response = accountController.bonifico(999L, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid accountId", response.getBody());
        verifyNoInteractions(fabrickService);
    }
}
