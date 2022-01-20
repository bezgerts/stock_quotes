package me.bezgerts.stockquotes.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http.rest-template")
public class RestTemplateProperties {
    private boolean enableDelayBeforeQuoteInfoRequest;
    private int delayTimeBeforeQuoteInfoRequestMs;
    private int maxTotalConnectionCount;
    private int connectionRequestTimeout;
}
