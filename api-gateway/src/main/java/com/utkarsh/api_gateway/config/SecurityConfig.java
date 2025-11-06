package com.utkarsh.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            http
                    // 1. Disable CSRF
                    .csrf(csrf -> csrf.disable())

                    // 2. Define authorization rules
                    .authorizeExchange(exchange -> exchange
                            // Any other request must be authenticated
                            .anyExchange().authenticated()
                    )

                    // 3. Configure as an OAuth2 Resource Server
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt -> {})
                    );

            return http.build();
        }
    }



