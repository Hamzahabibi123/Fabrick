package com.example.fabrick.controller;

import com.example.fabrick.dto.SaldoPayloadDto;
import com.example.fabrick.dto.SaldoResponseDto;
import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.exception.ExternalApiException;
import com.example.fabrick.service.FabrickService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
class AccountControllerTest {

     // TODO dovrei continuare su questo test non completato

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private FabrickService fabrickService;

    private final Long accountId = 14537780L;

    private SaldoResponseDto saldoResponse;
    private TransactionResponseDto transactionResponse;
    private BonificoResponseDto bonificoSuccessResponse;
    private BonificoResponseDto bonificoKoResponse;

    @BeforeEach
    void setUp() {
        // Saldo mock
        SaldoPayloadDto saldoPayload = new SaldoPayloadDto();
        saldoPayload.setAvailableBalance(new BigDecimal("123.45"));
        saldoResponse = new SaldoResponseDto();
        saldoResponse.setPayload(saldoPayload);


        fabrickService = Mockito.mock(FabrickService.class);
        mockMvc = Mockito.mock(MockMvc.class);
        objectMapper = Mockito.mock(ObjectMapper.class);
        // Transaction mock
        transactionResponse = new TransactionResponseDto();

        // Bonifico success
        bonificoSuccessResponse = new BonificoResponseDto("OK", null, "Transfer accepted");

        // Bonifico ko
        bonificoKoResponse = new BonificoResponseDto("KO", "API003", "API Error");
    }

    @Test
    void testGetBalance_Success() throws Exception {
        when(fabrickService.getBalance(eq(accountId))).thenReturn(new BigDecimal("123.45"));

        mockMvc.perform(get("/accounts/{accountId}/balance", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.availableBalance").value(123.45));
    }

    @Test
    void testGetBalance_InvalidAccount() throws Exception {
        when(fabrickService.getBalance(eq(accountId))).thenThrow(new ExternalApiException("API down"));

        mockMvc.perform(get("/accounts/{accountId}/balance", accountId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("API down"));
    }

    @Test
    void testGetTransactions_Success() throws Exception {
        when(fabrickService.getTransactions(eq(accountId), any(), any())).thenReturn(transactionResponse);

        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId)
                        .param("fromDate", "2025-01-01")
                        .param("toDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactionResponse)));
    }

    @Test
    void testGetTransactions_InvalidAccount() throws Exception {
        when(fabrickService.getTransactions(eq(accountId), any(), any()))
                .thenThrow(new ExternalApiException("API down"));

        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId)
                        .param("fromDate", "2025-01-01")
                        .param("toDate", "2025-01-31"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("API down"));
    }

    @Test
    void testMakeMoneyTransfer_Success() throws Exception {
        BonificoRequestDto requestDto = new BonificoRequestDto();

        when(fabrickService.makeMoneyTransfer(eq(accountId), any())).thenReturn(bonificoSuccessResponse);

        mockMvc.perform(post("/accounts/{accountId}/payments/money-transfers", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.description").value("Transfer accepted"));
    }

    @Test
    void testMakeMoneyTransfer_KO() throws Exception {
        BonificoRequestDto requestDto = new BonificoRequestDto();

        when(fabrickService.makeMoneyTransfer(eq(accountId), any())).thenReturn(bonificoKoResponse);

        mockMvc.perform(post("/accounts/{accountId}/payments/money-transfers", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("KO"))
                .andExpect(jsonPath("$.code").value("API003"));
    }
}
