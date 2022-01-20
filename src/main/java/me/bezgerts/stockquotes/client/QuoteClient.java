package me.bezgerts.stockquotes.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.configuration.HttpClientProperties;
import me.bezgerts.stockquotes.configuration.IexProperties;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import me.bezgerts.stockquotes.enumeration.RestClientType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteClient {

    private final HttpClient stockQuotesHttpClient;

    private final IexProperties iexProperties;
    private final HttpClientProperties httpClientProperties;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;
    private final RestTemplate restTemplate;
    @Value("${http.client-type}")
    private RestClientType restClientType;

    public CompletableFuture<QuoteInfoDto> getQuoteInfo(String stockCode) {
        CompletableFuture<QuoteInfoDto> result;
        switch (restClientType) {
            case HTTP_CLIENT:
                result = httpClientGetQuoteInfo(stockCode);
                break;
            case REST_TEMPLATE:
                result = restTemplateGetQuoteInfo(stockCode);
                break;
            default:
                throw new IllegalArgumentException("client type not supported");
        }
        return result;
    }

    public CompletableFuture<QuoteInfoDto> restTemplateGetQuoteInfo(String stockCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept", "application/json");

                Map<String, String> params = new HashMap<>();
                params.put("stock code", stockCode);

                String url = UriComponentsBuilder.fromHttpUrl(iexProperties.getBaseUrl() + iexProperties.getQuoteInfoPath())
                        .queryParam("token", iexProperties.getToken())
                        .buildAndExpand(params)
                        .toUriString();

                HttpEntity<QuoteInfoDto> entity = new HttpEntity<>(headers);
                ResponseEntity<QuoteInfoDto> response =
                        restTemplate.exchange(url, HttpMethod.GET, entity, QuoteInfoDto.class, params);

                return response.getBody();
            } catch (Exception e) {
                log.error("message: {}", e.getMessage(), e);
                return null;
            }

        }, executorService);
    }

    private CompletableFuture<QuoteInfoDto> httpClientGetQuoteInfo(String stockCode) {
        if (httpClientProperties.isEnableDelayBeforeQuoteInfoRequest()) {
            try {
                Thread.sleep(httpClientProperties.getDelayTimeBeforeQuoteInfoRequestMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<String, String> params = new HashMap<>();
        params.put("stock code", stockCode);

        URI uri = UriComponentsBuilder.fromHttpUrl(iexProperties.getBaseUrl() + iexProperties.getQuoteInfoPath())
                .queryParam("token", iexProperties.getToken())
                .buildAndExpand(params)
                .toUri();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return stockQuotesHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    } catch (Exception e) {
                        log.error("error message: {}, request: {}", e.getMessage(), request, e);
                        return null;
                    }
                }, executorService)
                .thenApply(this::checkTooManyRequestsStatusCode)
                .thenApply(HttpResponse::body)
                .thenApply(this::deserializeQuoteInfoDto)
                .exceptionally(e -> {
                    log.error("error message: {}", e.getMessage(), e);
                    return null;
                });
    }

    private QuoteInfoDto deserializeQuoteInfoDto(String s) {
        try {
            return mapper.readValue(s, QuoteInfoDto.class);
        } catch (Exception e) {
            log.error("error while parsing QuoteInfoDto: {}", e.getMessage(), e);
            return null;
        }
    }

    private HttpResponse<String> checkTooManyRequestsStatusCode(HttpResponse<String> httpResponse) {
        if (httpResponse.statusCode() != 429) {
            return httpResponse;
        } else {
            throw new RuntimeException("Too Many Requests");
        }
    }
}
