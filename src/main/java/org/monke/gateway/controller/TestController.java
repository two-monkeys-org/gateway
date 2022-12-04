package org.monke.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(){
        return Mono.just(ResponseEntity.ok().build());
    }
}
