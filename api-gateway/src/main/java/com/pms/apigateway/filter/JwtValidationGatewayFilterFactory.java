package com.pms.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

// by extending AbstractGatewayFilterFactory and implementing apply(), we're telling Spring Cloud Gateway to
// apply this logic to all the requests
@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    // Filter class: allows us to intercept requests, apply some logic and then decide whether to
    // continue processing or cancel the request

    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                             @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    @Override
    public GatewayFilter apply(Object config) {
        // exchange: object passed by Spring Cloud Gateway which contains all the properties of the request
        // chain: chain of filters (we can use it to access other filters)
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    // retrieve the response
                    .retrieve()
                    // tell the web client that there is no body in the response
                    .toBodilessEntity()
                    // all good, continue with the chain
                    .then(chain.filter(exchange));
        };
    }

}
