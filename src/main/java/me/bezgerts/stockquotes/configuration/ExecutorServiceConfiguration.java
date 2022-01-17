package me.bezgerts.stockquotes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfiguration {

    @Value("${stock-quotes.thread-count}")
    private int threadCount;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadCount);
    }
}
