package com.example.fabrick.service;

import com.example.fabrick.dto.bonifico.BonificoRequestDto;
import com.example.fabrick.dto.bonifico.BonificoResponseDto;
import com.example.fabrick.dto.SaldoResponseDto;
import com.example.fabrick.dto.TransactionResponseDto;
import com.example.fabrick.exception.ExternalApiException;
import com.example.fabrick.utli.Constants;
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

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final String authSchema;
    private final String timeZone;
    private final Logger log = LoggerFactory.getLogger(FabrickService.class);


    public FabrickService(RestTemplate restTemplate,
                          @Value("${fabrick.base-url}") String baseUrl,
                          @Value("${fabrick.api-key}") String apiKey,
                          @Value("${fabrick.auth-schema}") String authSchema,
                          @Value("${fabrick.X-Time-Zone}") String timeZone) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.authSchema = authSchema;
        this.timeZone = timeZone;
    }

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.set("Auth-Schema", authSchema);
        h.set("Api-Key", apiKey);
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("X-Time-Zone",timeZone);
        return h;
    }

    public BigDecimal getBalance(Long accountId) {
        String url = baseUrl + Constants.FABRICK_BALANCE_PATH;
        HttpEntity<Void> entity = new HttpEntity<>(headers());
        try {
            ResponseEntity<SaldoResponseDto> resp = restTemplate.exchange(
                    url, HttpMethod.GET, entity, SaldoResponseDto.class, Collections.singletonMap("accountId", accountId));
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null && resp.getBody().getPayload() != null ) {
                return resp.getBody().getPayload().getAvailableBalance();
            }
            throw new ExternalApiException("Unexpected response getting balance");
        } catch (Exception e) {
            log.error("Error calling Fabrick balance API", e);
            throw new ExternalApiException("Error calling Fabrick balance API: " + e.getMessage(), e);
        }
    }


    public TransactionResponseDto getTransactions(Long accountId, String from, String to) {
        String url = baseUrl + Constants.FABRICK_TRANSACTIONS_PATH;
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
            throw new ExternalApiException("Error calling Fabrick transactions API: " + e.getMessage(), e);
        }
    }


    public BonificoResponseDto makeMoneyTransfer(Long accountId, BonificoRequestDto payload) {
        String url = baseUrl + Constants.FABRICK_MONEY_TRANSFER_PATH;
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonPayload = mapper.writeValueAsString(payload);
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
            return new BonificoResponseDto("KO", "API000",
                    "Errore tecnico  La condizione BP049 non e' prevista per il conto id " + accountId);
        } catch (Exception e) {
            log.error("Error performing money transfer", e);
            throw new ExternalApiException("Error calling Fabrick money transfer API: " + e.getMessage(), e);
        }
    }

}
