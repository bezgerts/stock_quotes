package me.bezgerts.stockquotes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyDto {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("type")
    private String type;

    @JsonProperty("iexId")
    private String iexId;

    @JsonProperty("region")
    private String region;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("isEnabled")
    private Boolean isEnabled;

    @JsonProperty("figi")
    private String figi;

    @JsonProperty("cik")
    private String cik;
}
