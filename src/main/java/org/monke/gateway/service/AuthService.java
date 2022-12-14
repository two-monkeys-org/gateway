package org.monke.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.monke.gateway.entity.AuthenticatedSession;
import org.monke.gateway.entity.Credentials;
import org.monke.gateway.entity.ValidatedSessionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;

    private String getToken(ServerWebExchange exchange) {
        String authToken = null;
        try {
            authToken = Objects.requireNonNull(
                    exchange.getRequest().getHeaders().get("Authorization")
            ).get(0);
            log.info(authToken);
        } catch (Exception ignored) {
        }

        return authToken;
    }

    public void authenticateUser(ServerWebExchange exchange) {
        String authToken = getToken(exchange);
        if(authToken == null)
            return;

        this.authenticateWithSession(authToken);
    }

    private void authenticateWithSession(String authToken) {
        final ResponseEntity<ValidatedSessionResponse> response =  restTemplate.postForEntity("http://localhost:1234/validate/" + authToken,
                null,
                ValidatedSessionResponse.class);

        if(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        final ValidatedSessionResponse session = response.getBody();
        final String email = session.getEmail();
        final List<GrantedAuthority> authorities = Arrays.stream(
                restTemplate.getForObject(
                        "http://localhost:8050/role/" + email, String.class
                ).split(" "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        AuthenticatedSession authSession = new AuthenticatedSession()
                .setEmail(email)
                .setAuthorities(authorities)
                .withAuthenticated(true)
                .withPrincipal(email);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authSession);
    }

    public Mono<Authentication> validateUser(String username, String password) {
        try {
            final ResponseEntity<?> response = restTemplate.postForEntity("http://localhost:8050/user/validate",
                    new Credentials(username, password), Object.class);
        } catch(Exception e) {
            return Mono.empty();
        }

        final List<GrantedAuthority> authorities = Arrays.stream(
                        restTemplate.getForObject(
                                "http://localhost:8050/role/" + username, String.class
                        ).split(" "))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        AuthenticatedSession authSession = new AuthenticatedSession()
                .setEmail(username)
                .setAuthorities(authorities)
                .withAuthenticated(true)
                .withPrincipal(username);

        return Mono.just(authSession)
                .thenReturn((Authentication) authSession)
                .onErrorStop();
    }
}
