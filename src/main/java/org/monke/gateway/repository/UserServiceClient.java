package org.monke.gateway.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping(value = "/role")
    String getRoles();
}
