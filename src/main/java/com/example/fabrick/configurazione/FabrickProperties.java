package com.example.fabrick.configurazione;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fabrick") // tutti i property con prefisso "fabrick"
public class FabrickProperties {

    private Long accountId;
    private String balancePath;
    private String transactionsPath;
    private String moneyTransferPath;

}