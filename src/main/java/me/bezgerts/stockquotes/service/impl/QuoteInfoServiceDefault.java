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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class QuoteInfoServiceDefault implements QuoteInfoService {

    private final QuoteClient client;
    private final QuoteInfoRepository repository;
    private final QuoteInfoMapper quoteInfoMapper;

    @Override
    public void updateQuoteInfo(CompanyDto companyDto) {
        try {
            QuoteInfoDto quoteInfoDto = client.getQuoteInfo(companyDto.getSymbol());
            Optional<QuoteInfo> quoteInfoFromDbOptional = repository.findById(companyDto.getSymbol());
            if (quoteInfoFromDbOptional.isPresent()) {
                QuoteInfo quoteInfoFromDb = quoteInfoFromDbOptional.get();
                QuoteInfo quoteInfoFromRequest = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
                BigDecimal diff = getDiffBetweenPrice(quoteInfoDto, quoteInfoFromDb, quoteInfoFromRequest);
                BeanUtils.copyProperties(quoteInfoFromRequest, quoteInfoFromDb);
                quoteInfoFromDb.setDiffPrice(diff);
                repository.save(quoteInfoFromDb);
                log.info("thread: {}, updated quoteInfo: {}", Thread.currentThread().getName(), quoteInfoFromDb);
            } else {
                QuoteInfo quoteInfoEntity = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
                repository.save(quoteInfoEntity);
                log.info("thread: {}, updated quoteInfo: {}", Thread.currentThread().getName(), quoteInfoEntity);
            }
        } catch (Exception e) {
            log.error("e.getMessage: {} ; companyDto {}", e.getMessage(), companyDto, e);
        }
    }

    @Override
    public void updateQuoteInfo(List<CompanyDto> companyDtoList) {
        companyDtoList.forEach(this::updateQuoteInfo);
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
