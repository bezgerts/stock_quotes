package me.bezgerts.stockquotes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.client.QuoteClient;
import me.bezgerts.stockquotes.dto.CompanyDto;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import me.bezgerts.stockquotes.entity.QuoteInfo;
import me.bezgerts.stockquotes.mapper.QuoteInfoMapper;
import me.bezgerts.stockquotes.repository.QuoteInfoRepository;
import me.bezgerts.stockquotes.service.QuoteInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteInfoServiceDefault implements QuoteInfoService {

    private final QuoteClient client;
    private final QuoteInfoRepository repository;
    private final QuoteInfoMapper quoteInfoMapper;
    private final ExecutorService executorService;

    @Override
    public CompletableFuture<QuoteInfo> updateQuoteInfo(CompanyDto companyDto) {
        CompletableFuture<QuoteInfoDto> quoteInfoDtoCF = client.getQuoteInfoAsync(companyDto.getSymbol());
        CompletableFuture<Optional<QuoteInfo>> quoteInfoFromDbOptionalCF = findQuoteInfoFromDbCF(companyDto.getSymbol());
        return quoteInfoDtoCF.thenCombine(quoteInfoFromDbOptionalCF, this::updateQuoteInfo)
                .exceptionally(e -> {
                    log.error("e.getMessage: {} ; companyDto {}", e.getMessage(), companyDto, e);
                    return null;
                });
    }

    @Override
    @Transactional
    public void batchUpdateQuoteInfoList(List<QuoteInfo> quoteInfoList) {
        repository.saveAll(quoteInfoList);
    }

    @Override
    public List<QuoteInfoDto> getAllQuotes() {
        return repository.findAll().stream()
                .map(quoteInfoMapper::quoteInfoDtoFromQuoteInfo)
                .collect(Collectors.toList());
    }

    // TODO: 17.01.2022 посоветоваться, как обновлять (батчем или по одной записи)?
    private QuoteInfo updateQuoteInfo(QuoteInfoDto quoteInfoDto, Optional<QuoteInfo> quoteInfoOptional) {
        if (quoteInfoDto == null) {
            return null;
        }
        if (quoteInfoOptional.isPresent()) {
            QuoteInfo quoteInfoFromDb = quoteInfoOptional.get();
            QuoteInfo quoteInfoFromRequest = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
            BigDecimal diff = getDiffBetweenPrice(quoteInfoDto, quoteInfoFromDb, quoteInfoFromRequest);
            BeanUtils.copyProperties(quoteInfoFromRequest, quoteInfoFromDb);
            quoteInfoFromDb.setDiffPrice(diff);
            log.debug("thread: {}, updated quoteInfo: {}", Thread.currentThread().getName(), quoteInfoFromDb);
            return quoteInfoFromDb;
        } else {
            QuoteInfo quoteInfoEntity = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
            log.debug("thread: {}, updated quoteInfo: {}", Thread.currentThread().getName(), quoteInfoEntity);
            return quoteInfoEntity;
        }
    }

    private CompletableFuture<Optional<QuoteInfo>> findQuoteInfoFromDbCF(String symbol) {
        return CompletableFuture.supplyAsync(() -> repository.findById(symbol), executorService);
    }

    private BigDecimal getDiffBetweenPrice(QuoteInfoDto quoteInfoDto, QuoteInfo quoteInfoFromDb, QuoteInfo quoteInfoFromRequest) {
        BigDecimal result;
        if (quoteInfoFromRequest.getLatestPrice() != null) {
            result = quoteInfoFromRequest.getLatestPrice().subtract(quoteInfoFromDb.getLatestPrice());
        } else {
            result = quoteInfoDto.getLatestPrice();
        }
        return result;
    }
}
