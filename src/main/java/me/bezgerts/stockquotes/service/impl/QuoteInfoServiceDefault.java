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
import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuoteInfoServiceDefault implements QuoteInfoService {

    private final QuoteClient client;
    private final QuoteInfoRepository repository;
    private final QuoteInfoMapper quoteInfoMapper;

    @Override
    public void updateQuoteInfo(CompanyDto companyDto) {
        Optional<QuoteInfo> quoteInfoFromDbOptional = repository.findById(companyDto.getSymbol());
        QuoteInfoDto quoteInfoDto = client.getQuoteInfo(companyDto.getSymbol());
        if (quoteInfoFromDbOptional.isPresent()) {
            QuoteInfo quoteInfoFromDb = quoteInfoFromDbOptional.get();
            QuoteInfo quoteInfoFromRequest = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
            BigDecimal diff = quoteInfoFromRequest.getLatestPrice().subtract(quoteInfoFromDb.getLatestPrice());
            BeanUtils.copyProperties(quoteInfoFromRequest, quoteInfoFromDb);
            quoteInfoFromDb.setDiffPrice(diff);
            repository.save(quoteInfoFromDb);
            log.info("updated quoteInfo: {}", quoteInfoFromDb);
        } else {
            QuoteInfo quoteInfoEntity = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
            repository.save(quoteInfoEntity);
            log.info("updated quoteInfo: {}", quoteInfoEntity);
        }
    }
}
