package org.monke.gateway.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class TestController {

    @GetMapping("current")
    public String getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("roles")
    public Collection<? extends GrantedAuthority> getRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
