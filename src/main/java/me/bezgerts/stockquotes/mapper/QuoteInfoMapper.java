package me.bezgerts.stockquotes.mapper;

import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import me.bezgerts.stockquotes.entity.QuoteInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class QuoteInfoMapper {

    public QuoteInfo quoteInfoFromQuoteInfoDto(QuoteInfoDto quoteInfoDto) {
        QuoteInfo result = new QuoteInfo();
        BeanUtils.copyProperties(quoteInfoDto, result);
        return result;
    }

    public QuoteInfoDto quoteInfoDtoFromQuoteInfo(QuoteInfo quoteInfo) {
        QuoteInfoDto result = new QuoteInfoDto();
        BeanUtils.copyProperties(quoteInfo, result);
        return result;
    }
}
