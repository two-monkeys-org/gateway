package org.monke.gateway.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.monke.gateway.service.AuthService;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHORIZATION;

@Slf4j
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthService authService;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .httpBasic()
                .and().formLogin()
                .and()
                .addFilterBefore(new AuthenticationFilter(authService), AUTHORIZATION);

        return http.build();
    }
}