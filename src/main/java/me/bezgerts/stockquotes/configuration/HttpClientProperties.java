package me.bezgerts.stockquotes.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http.http-client")
public class HttpClientProperties {
    private boolean enableDelayBeforeQuoteInfoRequest;
    private int delayTimeBeforeQuoteInfoRequestMs;
}
