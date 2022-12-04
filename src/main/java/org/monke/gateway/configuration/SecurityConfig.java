package org.monke.gateway.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.monke.gateway.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(){
        UserDetails user1 = User.builder()
                .username("admin")
                .password(this.passwordEncoder().encode("admin"))
                .roles("admin")
                .build();

        return new MapReactiveUserDetailsService(user1);
    }

    @Bean
    public SecurityWebFilterChain chain(ServerHttpSecurity http){
        http
                .csrf().disable()
                .formLogin()
                .authenticationManager(authenticationManager)
                .authenticationSuccessHandler(new WebFilterChainServerAuthenticationSuccessHandler())
                .authenticationFailureHandler(((webFilterExchange, exception) -> Mono.error(exception)))
                .and()
                .authorizeExchange()
                .pathMatchers("/login", "/validate").permitAll()
                .pathMatchers("/v3/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/**").permitAll()
                .pathMatchers( HttpMethod.POST, "/user-service/user").permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
}