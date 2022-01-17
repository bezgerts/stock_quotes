package me.bezgerts.stockquotes.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.configuration.HttpClientProperties;
import me.bezgerts.stockquotes.configuration.IexProperties;
import me.bezgerts.stockquotes.dto.QuoteInfoDto;
import org.springframework.stereotype.Component;
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

    private final IexProperties iexProperties;
    private final HttpClientProperties httpClientProperties;
    private final ObjectMapper mapper;
    private final ExecutorService executorService;

    public CompletableFuture<QuoteInfoDto> getQuoteInfoAsync(String stockCode) {
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

        HttpClient httpClient = HttpClient.newBuilder()
                .executor(executorService)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
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
