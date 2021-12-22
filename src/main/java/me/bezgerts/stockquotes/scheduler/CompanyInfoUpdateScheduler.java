package me.bezgerts.stockquotes.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.service.CompanyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyInfoUpdateScheduler {

    private final CompanyService companyService;

    @Scheduled(fixedDelayString = "${stock-quotes.scheduler.company-update-period-ms}")
    public void updateCompanyInfo() {
        List<CompanyDto> companyDtoList = companyService.getAllCompanies();
        companyService.updateCompanyList(companyDtoList);
    }
}
