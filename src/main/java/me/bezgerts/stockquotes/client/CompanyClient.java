package me.bezgerts.stockquotes.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bezgerts.stockquotes.configuration.IexProperties;
import me.bezgerts.stockquotes.dto.CompanyDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyClient {

    private final RestTemplate restTemplate;
    private final IexProperties iexProperties;

    public List<CompanyDto> getAllCompanies() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        String url  = UriComponentsBuilder.fromHttpUrl(iexProperties.getBaseUrl() + iexProperties.getSymbolsPath())
                .queryParam("token", iexProperties.getToken())
                .toUriString();

        HttpEntity<CompanyDto> entity = new HttpEntity<>(headers);
        ResponseEntity<List<CompanyDto>> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}
