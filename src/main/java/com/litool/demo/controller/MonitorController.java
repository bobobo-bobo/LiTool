package com.litool.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author libinzhou
 * @date 2025/1/28 13:33
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @GetMapping( "/health")
    public String health() {
        return "health";
    }

    @GetMapping( "/ask/{ask}")
    public String askByPath(@PathVariable("ask") String ask) {
        return ask;
    }

    @GetMapping( "/askByParam")
    public String askByParam(String ask) {
        return ask;
    }
}
