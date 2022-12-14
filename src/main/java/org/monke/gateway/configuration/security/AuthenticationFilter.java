package org.monke.gateway.configuration.security;

import lombok.RequiredArgsConstructor;
import org.monke.gateway.service.AuthService;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticationFilter extends MapReactiveUserDetailsService implements WebFilter {
    private final AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        authService.authenticateUser(exchange);

        return chain.filter(exchange);
    }
}
