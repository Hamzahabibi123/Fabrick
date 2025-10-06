package com.example.fabrick.controller;

import com.example.fabrick.configurazione.FabrickProperties;
import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.service.FabrickService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FabrickService fabrickService;

    @MockitoBean
    private FabrickProperties fabrickProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long VALID_ACCOUNT_ID = 12345L;
    private final Long INVALID_ACCOUNT_ID = 999L;

    @BeforeEach
    void setUp() {
        Mockito.when(fabrickProperties.getAccountId()).thenReturn(VALID_ACCOUNT_ID);
    }

    @Test
    void testGetBalance_ValidAccount() throws Exception {
        Mockito.when(fabrickService.getBalance(VALID_ACCOUNT_ID)).thenReturn(BigDecimal.valueOf(2500.50));

        mockMvc.perform(get("/api/accounts/{accountId}/balance", VALID_ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().string("2500.5"));
    }

    @Test
    void testGetBalance_InvalidAccount() throws Exception {
        mockMvc.perform(get("/api/accounts/{accountId}/balance", INVALID_ACCOUNT_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid accountId"));
    }

    @Test
    void testGetTransactions_ValidAccount() throws Exception {
        TransactionResponseDto resp = new TransactionResponseDto();
        Mockito.when(fabrickService.getTransactions(eq(VALID_ACCOUNT_ID), any(), any()))
                .thenReturn(resp);

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", VALID_ACCOUNT_ID)
                        .param("fromAccountingDate", "2024-01-01")
                        .param("toAccountingDate", "2024-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void testBonifico_ValidAccount() throws Exception {
        BonificoRequestDto req = new BonificoRequestDto();
        BonificoResponseDto resp = new BonificoResponseDto();

        Mockito.when(fabrickService.makeMoneyTransfer(eq(VALID_ACCOUNT_ID), any()))
                .thenReturn(resp);

        mockMvc.perform(post("/api/accounts/{accountId}/payments/money-transfers", VALID_ACCOUNT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testBonifico_InvalidAccount() throws Exception {
        BonificoRequestDto req = new BonificoRequestDto();

        mockMvc.perform(post("/api/accounts/{accountId}/payments/money-transfers", INVALID_ACCOUNT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

    }
}

