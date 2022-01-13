package me.bezgerts.stockquotes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDto {

    @JsonProperty("companyDto")
    private CompanyDto companyDto;

    @JsonProperty("quoteInfoDto")
    private QuoteInfoDto quoteInfoDto;
}
