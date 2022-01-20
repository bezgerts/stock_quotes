package me.bezgerts.stockquotes.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class HttpClientConfiguration {

    @Bean(name = "stockQuotesHttpClient")
    public HttpClient stockQuotesHttpClient() {
        return HttpClient.newBuilder().build();
    }
}
