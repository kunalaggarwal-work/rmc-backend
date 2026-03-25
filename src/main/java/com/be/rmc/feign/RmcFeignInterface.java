package com.be.rmc.feign;

import com.be.rmc.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "warmc-service", url = "http://warmc-service")
public interface RmcFeignInterface {

    @PostMapping("/validateUser")
    public ResponseEntity<User> validateUser(@RequestParam String userId);
}
