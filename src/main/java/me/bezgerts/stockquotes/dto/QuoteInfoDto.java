package me.bezgerts.stockquotes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteInfoDto {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("volume")
    private Long volume;

    @JsonProperty("previousVolume")
    private Long previousVolume;

    @JsonProperty("diffPrice")
    private BigDecimal diffPrice;

    @JsonProperty("latestPrice")
    private BigDecimal latestPrice;
}
