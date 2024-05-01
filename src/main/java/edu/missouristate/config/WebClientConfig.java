package edu.missouristate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a WebClient bean for making HTTP requests.
     * This bean is set up with default settings suitable for various HTTP operations
     *
     * @return A configured instance of WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}