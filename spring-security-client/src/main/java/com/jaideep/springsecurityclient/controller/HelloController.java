package com.jaideep.springsecurityclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1/api")
public class HelloController {
    @GetMapping("/hello")
    public String getHello() {
        return "This is hello endpoint";
    }
}
