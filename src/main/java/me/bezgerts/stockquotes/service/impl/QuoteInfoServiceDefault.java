package me.bezgerts.stockquotes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.client.QuoteClient;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import me.bezgerts.stockquotes.entity.QuoteInfo;
import me.bezgerts.stockquotes.mapper.QuoteInfoMapper;
import me.bezgerts.stockquotes.repository.QuoteInfoRepository;
import me.bezgerts.stockquotes.service.QuoteInfoService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuoteInfoServiceDefault implements QuoteInfoService {

    private final QuoteClient client;
    private final QuoteInfoRepository repository;
    private final QuoteInfoMapper quoteInfoMapper;

    @Override
    public void saveQuoteInfo(String symbol) {
        QuoteInfoDto quoteInfoDto = client.getQuoteInfo(symbol);
        QuoteInfo quoteInfoEntity = quoteInfoMapper.quoteInfoFromQuoteInfoDto(quoteInfoDto);
        repository.save(quoteInfoEntity);
    }
}
