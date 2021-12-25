package me.bezgerts.stockquotes.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.service.CompanyService;
import me.bezgerts.stockquotes.service.QuoteInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyInfoUpdateScheduler {

    @Value("${stock-quotes.thread-count}")
    private int threadCount;

    private final CompanyService companyService;
    private final QuoteInfoService quoteInfoService;

    @Scheduled(fixedDelayString = "${stock-quotes.scheduler.company-update-period-ms}")
    public void updateCompanyInfo() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<CompanyDto> companyDtoList = companyService.getAllCompanies();
        List<CompanyDto> updatedCompanyDtoList = companyService.updateCompanyList(companyDtoList);
        List<List<CompanyDto>> partitions = splitByPartitions(updatedCompanyDtoList, threadCount);
        List<Callable<Void>> callables = new ArrayList<>(threadCount);
        for (List<CompanyDto> partition :
                partitions) {
            callables.add(() -> {
                quoteInfoService.updateQuoteInfo(partition);
                return null;
            });
        }
        executorService.invokeAll(callables);
        shutdownExecutorService(executorService);
    }

    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private List<List<CompanyDto>> splitByPartitions(List<CompanyDto> companyDtoList, final int n) {
        return new ArrayList<>(
                IntStream.range(0, companyDtoList.size())
                        .boxed()
                        .collect(Collectors.groupingBy(e -> e % n, Collectors.mapping(companyDtoList::get, Collectors.toList())))
                        .values());
    }
}
