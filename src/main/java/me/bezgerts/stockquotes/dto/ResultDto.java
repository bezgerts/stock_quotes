package me.bezgerts.stockquotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDto {
    private CompanyDto companyDto;
    private QuoteInfoDto quoteInfoDto;
}
