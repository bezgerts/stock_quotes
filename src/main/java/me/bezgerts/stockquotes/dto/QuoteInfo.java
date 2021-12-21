package me.bezgerts.stockquotes.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteInfo {
    private String symbol;
    private String companyName;
    private Long volume;
    private Long previousVolume;
    private BigDecimal latestPrice;
}
