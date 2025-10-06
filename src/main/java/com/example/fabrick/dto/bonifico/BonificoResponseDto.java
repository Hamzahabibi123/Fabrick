package com.example.fabrick.dto.bonifico;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BonificoResponseDto {
    @JsonProperty("status")
    private String status;
    @JsonProperty("code")
    private String code;
    @JsonProperty("description")
    private String description;

    public BonificoResponseDto(String status, String code, String description) {
        this.status=status;
        this.code=code;
        this.description=description;
    }

    //lo aggiungo per i test sul AccountControllerTEst
    public BonificoResponseDto() {}

}
