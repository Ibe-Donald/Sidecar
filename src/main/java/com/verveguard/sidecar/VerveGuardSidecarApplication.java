package com.verveguard.sidecar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VerveGuardSidecarApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerveGuardSidecarApplication.class, args);
    }

}
