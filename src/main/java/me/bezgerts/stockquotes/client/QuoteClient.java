package me.bezgerts.stockquotes.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.configuration.IexProperties;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteClient {

    private final RestTemplate restTemplate;
    private final IexProperties iexProperties;

    public QuoteInfoDto getQuoteInfo(String stockCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        Map<String, String> params = new HashMap<>();
        params.put("stock code", stockCode);

        String url  = UriComponentsBuilder.fromHttpUrl(iexProperties.getBaseUrl() + iexProperties.getQuoteInfoPath())
                .queryParam("token", iexProperties.getToken())
                .buildAndExpand(params)
                .toUriString();

        HttpEntity<QuoteInfoDto> entity = new HttpEntity<>(headers);
        ResponseEntity<QuoteInfoDto> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, QuoteInfoDto.class, params);

        return response.getBody();
    }
}
