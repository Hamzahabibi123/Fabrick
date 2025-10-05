package com.example.fabrick.service;

import com.example.fabrick.configurazione.FabrickProperties;
import com.example.fabrick.dto.SaldoPayloadDto;
import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.dto.SaldoResponseDto;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.exception.ExternalApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FabrickServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FabrickService fabrickService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FabrickProperties fabrickProperties;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(fabrickProperties.getAccountId()).thenReturn(14537780L);
        when(fabrickProperties.getBalancePath()).thenReturn("/api/gbs/banking/v4.0/accounts/{accountId}/balance");
        when(fabrickProperties.getTransactionsPath()).thenReturn("/api/gbs/banking/v4.0/accounts/{accountId}/transactions");
        when(fabrickProperties.getMoneyTransferPath()).thenReturn("/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers");
    }

    @Test
    void testGetBalance_Success() {
        // create payload instance
        SaldoPayloadDto payload = new SaldoPayloadDto();
        payload.setAvailableBalance(new BigDecimal("123.45"));

        // create response DTO and set payload
        SaldoResponseDto respDto = new SaldoResponseDto();
        respDto.setPayload(payload);

        // mock RestTemplate
        ResponseEntity<SaldoResponseDto> responseEntity = new ResponseEntity<>(respDto, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(SaldoResponseDto.class),
                anyMap()
        )).thenReturn(responseEntity);

        // call service
        BigDecimal balance = fabrickService.getBalance(14537780L);

        // assert
        assertEquals(new BigDecimal("123.45"), balance);
    }


    void testGetBalance_Exception() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(SaldoResponseDto.class),
                anyMap()
        )).thenThrow(new RuntimeException("API down"));

        assertThrows(ExternalApiException.class, () ->
                fabrickService.getBalance(14537780L)
        );
    }

    @Test
    void testGetTransactions_Success() {
        TransactionResponseDto txResp = new TransactionResponseDto();
        ResponseEntity<TransactionResponseDto> responseEntity = new ResponseEntity<>(txResp, HttpStatus.OK);

        when(restTemplate.exchange(
                any(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(TransactionResponseDto.class)
        )).thenReturn(responseEntity);

        TransactionResponseDto resp = fabrickService.getTransactions(14537780L, "2025-01-01", "2025-01-31");
        assertNotNull(resp);
    }

    @Test
    void testMakeMoneyTransfer_Success() {
        BonificoRequestDto requestDto = new BonificoRequestDto();
        BonificoResponseDto respDto = new BonificoResponseDto("OK", null, "Transfer accepted");
        ResponseEntity<BonificoResponseDto> responseEntity = new ResponseEntity<>(respDto, HttpStatus.OK);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(BonificoResponseDto.class),
                anyLong()
        )).thenReturn(responseEntity);

        BonificoResponseDto result = fabrickService.makeMoneyTransfer(14537780L, requestDto);
        assertEquals("OK", result.getStatus());
        assertEquals("Transfer accepted", result.getDescription());
    }

    @Test
    void testMakeMoneyTransfer_KO() {
        BonificoRequestDto requestDto = new BonificoRequestDto();
        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(BonificoResponseDto.class),
                anyLong()
        )).thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, null, null));

        BonificoResponseDto result = fabrickService.makeMoneyTransfer(14537780L, requestDto);
        assertEquals("KO", result.getStatus());
        assertEquals("API003", result.getCode());
    }
}
