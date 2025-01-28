package com.litool.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author libinzhou
 * @date 2025/1/28 13:33
 */
@RestController("monitor")
public class MonitorController {

    @RequestMapping(value = "health")
    public String health(String ask) {
        return ask;
    }
}
