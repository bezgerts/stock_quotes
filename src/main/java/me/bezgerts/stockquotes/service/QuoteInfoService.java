package me.bezgerts.stockquotes.service;

import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;

import java.util.List;

public interface QuoteInfoService {
    void updateQuoteInfo(CompanyDto companyDto);
    void updateQuoteInfo(List<CompanyDto> companyDtoList);
    List<QuoteInfoDto> getAllQuotes();
}
