package org.monke.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class TestController {

    @CrossOrigin
    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(WebSession session){

        return Mono.just(
                ResponseEntity.ok(
                     Map.of("SESSION", session.getId())
                ));
    }
}
