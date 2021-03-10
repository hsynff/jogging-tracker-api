package com.jogging.tracker.config;

import com.jogging.tracker.util.rsql.RsqlSearchOperation;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class Beans {

    @Bean
    public RSQLParser rsqlParser() {
        Set<ComparisonOperator> operators = Arrays.stream(RsqlSearchOperation.values())
                .map(RsqlSearchOperation::getOperator).collect(Collectors.toSet());

        return new RSQLParser(operators);
    }


    @Bean
    public RestTemplate weatherApiRestTemplate(@Value("${rapidApi.key}") String rapidApiKey,
                                               @Value("${rapidApi.host}") String rapidApiHost,
                                               @Value("${rapidApi.baseUrl}") String baseUrl) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                HttpHeaders headers = httpRequest.getHeaders();
                headers.add("x-rapidapi-key", rapidApiKey);
                headers.add("x-rapidapi-host", rapidApiHost);

                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        }));
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));

        return restTemplate;
    }
}
