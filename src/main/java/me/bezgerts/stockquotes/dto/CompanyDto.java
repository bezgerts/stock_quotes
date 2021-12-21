package me.bezgerts.stockquotes.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CompanyDto {
    private String symbol;
    private String name;
    private LocalDate date;
    private String type;
    private String iexId;
    private String region;
    private String currency;
    private boolean isEnabled;
    private String figi;
    private String cik;
}
