package me.bezgerts.stockquotes.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.entity.QuoteInfo;
import me.bezgerts.stockquotes.service.CompanyService;
import me.bezgerts.stockquotes.service.QuoteInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyInfoUpdateScheduler {

    private final CompanyService companyService;
    private final QuoteInfoService quoteInfoService;

    @Scheduled(fixedDelayString = "${stock-quotes.scheduler.company-update-period-ms}")
    public void updateCompanyInfo() {
        // получаем список компаний
        List<CompanyDto> companyDtoList = companyService.getAllCompanies();

        // обновляем информацию по каждой из компаний
        List<CompanyDto> updatedCompanyDtoList = companyService.updateCompanyList(companyDtoList);

        // обновляем в несколько потоков QuoteInfo по обновленному списку компаний
        List<QuoteInfo> updatedQuoteInfoCFList = updatedCompanyDtoList.stream()
                .map(quoteInfoService::updateQuoteInfo)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        log.info("count of updated quoteInfo: {}", updatedQuoteInfoCFList.size());
    }
}
