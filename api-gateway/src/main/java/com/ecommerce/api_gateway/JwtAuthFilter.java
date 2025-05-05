package com.ecommerce.api_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtProvider jwtProvider;

    @Autowired
    @Lazy
    private final RestTemplate restTemplate;

    public JwtAuthFilter(JwtProvider jwtProvider, RestTemplate restTemplate) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.restTemplate = restTemplate;
    }

    @Override
    public GatewayFilter apply(JwtAuthFilter.Config config) {
        return (exchange, chain) -> {
            String token = jwtProvider.resolveToken(exchange.getRequest());
            boolean isTokenValid = jwtProvider.isTokenValid(token);

            if (token != null && isTokenValid) {
                String email = jwtProvider.extractEmail(token);
                UserResponse user = restTemplate.getForObject(
                        String.format("http://%s:8080%s", "http://user-service", "/api/v1/user" + email),
                        UserResponse.class,
                        email
                );

                if (!user.isEnabled()) {
                    throw new JwtAuthenticationException("Email not activated");
                }
                exchange.getRequest()
                        .mutate()
                        .header("auth-user-id", String.valueOf(user.getId()))
                        .build();
                return chain.filter(exchange);
            } else {
                throw new JwtAuthenticationException("Token expired");
            }
        };
    }

    public static class Config {

    }
}