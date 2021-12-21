package me.bezgerts.stockquotes.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "stock-quotes.iex")
public class IexProperties {
    private String baseUrl;
    private String symbolsPath;
    private String token;
}
