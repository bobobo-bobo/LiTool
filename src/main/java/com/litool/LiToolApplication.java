package com.litool;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LiToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiToolApplication.class, args);
    }

}
