package edu.missouristate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class FaviconConfig {
    /**
     * Configures the handler mapping for favicon requests to ensure the favicon is served for every request without additional configuration.
     * This method sets the highest priority order to handle favicon.ico requests across the application
     *
     * @return A SimpleUrlHandlerMapping configured for handling favicon requests.
     */
    @Bean
    public SimpleUrlHandlerMapping faviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        return mapping;
    }

    /**
     * Creates a request handler for serving favicon.ico. This method specifies the resource location of the favicon
     *
     * @return A ResourceHttpRequestHandler that serves the favicon from the classpath
     */
    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        requestHandler.setLocations(Arrays.<Resource>asList(new ClassPathResource("/")));
        return requestHandler;
    }
}
