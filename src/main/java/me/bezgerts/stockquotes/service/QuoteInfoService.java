package me.bezgerts.stockquotes.service;

import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import me.bezgerts.stockquotes.entity.QuoteInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface QuoteInfoService {
    CompletableFuture<QuoteInfo> updateQuoteInfo(CompanyDto companyDto);

    void batchUpdateQuoteInfoList(List<QuoteInfo> quoteInfoList);

    List<QuoteInfoDto> getAllQuotes();
}
