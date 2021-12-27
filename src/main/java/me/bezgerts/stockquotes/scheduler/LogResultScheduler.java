package me.bezgerts.stockquotes.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import me.bezgerts.stockquotes.dto.ResultDto;
import me.bezgerts.stockquotes.service.CompanyService;
import me.bezgerts.stockquotes.service.QuoteInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogResultScheduler {

    @Value("${stock-quotes.count-top-volume-companies}")
    private int countOfTopVolumeCompanies;

    @Value("${stock-quotes.count-top-volatile-companies}")
    private int countOfTopVolatileCompanies;

    private final QuoteInfoService quoteInfoService;
    private final CompanyService companyService;

    @Scheduled(fixedRateString = "${stock-quotes.scheduler.log-result-period-ms}")
    public void logResultToConsole() {
        List<QuoteInfoDto> quoteInfoDtoList = quoteInfoService.getAllQuotes();
        logTopVolumeCompanies(quoteInfoDtoList, countOfTopVolumeCompanies);
        logTopVolatileCompanies(quoteInfoDtoList, countOfTopVolatileCompanies);
    }

    private void logTopVolumeCompanies(List<QuoteInfoDto> quoteInfoDtoList, int count) {
        log.info("=== top volumed companies ===");
        List<QuoteInfoDto> sortedQuoteInfoDtoList = quoteInfoDtoList.stream()
                .filter(quoteInfoDto -> quoteInfoDto.getVolume() != null)
                .sorted(LogResultScheduler::sortByVolume)
                .collect(Collectors.toList());
        printResultDto(getResultDtoListByQuoteInfoDto(sortedQuoteInfoDtoList, count));
    }

    private void logTopVolatileCompanies(List<QuoteInfoDto> quoteInfoDtoList, int count) {
        log.info("=== top volatile companies ===");
        List<QuoteInfoDto> sortedQuoteInfoDtoList = quoteInfoDtoList.stream()
                .filter(quoteInfoDto -> quoteInfoDto.getDiffPrice() != null)
                .map(LogResultScheduler::replaceWithModuloDiffPrice)
                .sorted(LogResultScheduler::sortByDiffPrice)
                .collect(Collectors.toList());
        printResultDto(getResultDtoListByQuoteInfoDto(sortedQuoteInfoDtoList, count));
    }

    private static QuoteInfoDto replaceWithModuloDiffPrice(QuoteInfoDto quoteInfoDto) {
        QuoteInfoDto moduleQuoteInfoDto = new QuoteInfoDto();
        BeanUtils.copyProperties(quoteInfoDto, moduleQuoteInfoDto);
        if (moduleQuoteInfoDto.getDiffPrice().floatValue() < 0) {
            BigDecimal positiveDiffPrice = moduleQuoteInfoDto.getDiffPrice().multiply(BigDecimal.valueOf(-1L));
            moduleQuoteInfoDto.setDiffPrice(positiveDiffPrice);
        }
        return moduleQuoteInfoDto;
    }

    private void printResultDto(List<ResultDto> resultDtoList) {
        resultDtoList.forEach(resultDto -> {
            log.info("companyName: {}, volume {}, diffPrice {}, resultDto: {}",
                    resultDto.getCompanyDto().getName(),
                    resultDto.getQuoteInfoDto().getVolume(),
                    resultDto.getQuoteInfoDto().getDiffPrice(),
                    resultDto);
        });
    }

    private List<ResultDto> getResultDtoListByQuoteInfoDto(List<QuoteInfoDto> quoteInfoDtoList, int count) {
        return quoteInfoDtoList.stream()
                .limit(count)
                .map(this::quoteInfoDtoToResultDto)
                .collect(Collectors.toList());
    }

    private static int sortByVolume(QuoteInfoDto quoteInfoDto1, QuoteInfoDto quoteInfoDto2) {
        return quoteInfoDto2.getVolume().compareTo(quoteInfoDto1.getVolume());
    }

    private static int sortByDiffPrice(QuoteInfoDto quoteInfoDto1, QuoteInfoDto quoteInfoDto2) {
        return quoteInfoDto2.getDiffPrice().compareTo(quoteInfoDto1.getDiffPrice());
    }

    private ResultDto quoteInfoDtoToResultDto(QuoteInfoDto quoteInfoDto) {
        CompanyDto companyDto = companyService.findCompanyBySymbol(quoteInfoDto.getSymbol());
        return new ResultDto(companyDto, quoteInfoDto);
    }
}
