package org.monke.gateway.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.monke.gateway.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.FIRST;

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
                .pathMatchers("/authorization-server/login", "/user-service/user/validate").permitAll()
                .pathMatchers(HttpMethod.POST, "/user-service/user").permitAll()
                .pathMatchers("/authorization-server/login", "authorization-server/validate/**").permitAll()
                .anyExchange().permitAll()
                .and()
                .httpBasic()
                .and().formLogin()
                .and()
                .addFilterBefore(new AuthenticationFilter(authService), FIRST);

        return http.build();
    }
}