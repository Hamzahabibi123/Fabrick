package com.example.fabrick.service;

import com.example.fabrick.configurazione.FabrickProperties;
import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.dto.SaldoResponseDto;
import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.exception.ExternalApiException;

import com.example.fabrick.util.ErrorCodes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class FabrickService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final FabrickProperties fabrickProperties;
    private final String baseUrl;
    private final String apiKey;
    private final String authSchema;
    private final String timeZone;
    private final Logger log = LoggerFactory.getLogger(FabrickService.class);


    public FabrickService(ObjectMapper objectMapper, RestTemplate restTemplate, FabrickProperties fabrickProperties,
                          @Value("${fabrick.base-url}") String baseUrl,
                          @Value("${fabrick.api-key}") String apiKey,
                          @Value("${fabrick.auth-schema}") String authSchema,
                          @Value("${fabrick.X-Time-Zone}") String timeZone) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.fabrickProperties = fabrickProperties;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.authSchema = authSchema;
        this.timeZone = timeZone;
    }

    /** qui prepara gli header comuni per tutte le chiamate Fabrick */
    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.set("Auth-Schema", authSchema);
        h.set("Api-Key", apiKey);
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("X-Time-Zone",timeZone);
        return h;
    }

    public BigDecimal getBalance(Long accountId) {
        String url = baseUrl + fabrickProperties.getBalancePath();
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        try {
            ResponseEntity<SaldoResponseDto> resp = restTemplate.exchange(
                    url, HttpMethod.GET, entity, SaldoResponseDto.class, Collections.singletonMap("accountId", accountId));
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null && resp.getBody().getPayload() != null ) {
                return resp.getBody().getPayload().getAvailableBalance();
            }
            throw new ExternalApiException(ErrorCodes.MSG_BALANCE_ERROR);
        } catch (Exception e) {
            log.error("Error calling Fabrick balance API", e);
            throw new ExternalApiException(ErrorCodes.MSG_BALANCE_ERROR + " " + e.getMessage(), e);
        }
    }


    public TransactionResponseDto getTransactions(Long accountId, String from, String to) {
        String url = baseUrl + fabrickProperties.getTransactionsPath();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("fromAccountingDate", from)
                .queryParam("toAccountingDate", to);
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        try {
            ResponseEntity<TransactionResponseDto> resp = restTemplate.exchange(
                    builder.buildAndExpand(Collections.singletonMap("accountId", accountId)).toUri(),
                    HttpMethod.GET, entity, TransactionResponseDto.class);
            return resp.getBody();
        } catch (Exception e) {
            log.error("Error calling Fabrick transactions API", e);
            throw new ExternalApiException(ErrorCodes.MSG_TRANSACTIONS_ERROR + " " + e.getMessage(), e);
        }
    }


    public BonificoResponseDto makeMoneyTransfer(Long accountId, BonificoRequestDto payload) {
        String url = baseUrl + fabrickProperties.getMoneyTransferPath();

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers());
            ResponseEntity<BonificoResponseDto> resp = restTemplate.postForEntity(
                    url,
                    entity,
                    BonificoResponseDto.class,
                    accountId
            );

            return resp.getBody(); // direct mapping
        } catch (HttpClientErrorException e) {
            log.warn("Money transfer KO: {}", e.getResponseBodyAsString());
            return new BonificoResponseDto(
                    "KO",
                    ErrorCodes.API_TRANSFER_ERROR,
                    ErrorCodes.MSG_TRANSFER_ERROR + accountId
            );
        } catch (Exception e) {
            log.error("Error performing money transfer", e);
            throw new ExternalApiException(ErrorCodes.MSG_TRANSFER_ERROR + e.getMessage(), e);
        }
    }

}
