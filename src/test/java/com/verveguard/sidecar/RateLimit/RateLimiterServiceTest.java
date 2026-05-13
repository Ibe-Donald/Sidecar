package com.verveguard.sidecar.RateLimit;


import com.verveguard.sidecar.exception.RateLimitException;
import com.verveguard.sidecar.Service.RateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;
    private final String testIp = "192.168.1.100";


    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    void whenFirstRequest_thenDoNotThrowException() {
        assertDoesNotThrow(() -> {
            rateLimiterService.checkLimit(testIp);
        });
    }

    @Test
    void whenExactlyFiveRequests_thenDoNotThrowException() {
        assertDoesNotThrow(() -> {
            // Simulate 5 requests from the exact same IP
            for (int i = 0; i < 5; i++) {
                rateLimiterService.checkLimit(testIp);
            }
        });
    }

    @Test
    void whenDifferentIpsRequest_thenTrackSeparately() {

        String ip1 = "10.0.0.1";
        String ip2 = "10.0.0.2";

        // Max out IP 1
        for (int i = 0; i < 5; i++) {
            rateLimiterService.checkLimit(ip1);
        }

        // Even though the system has processed 5 requests, IP 2 is a new user and should be allowed in
        assertDoesNotThrow(() -> {
            rateLimiterService.checkLimit(ip2);
        });

        // But IP 1 should still be blocked
        assertThrows(RateLimitException.class, () -> {
            rateLimiterService.checkLimit(ip1);
        });
    }
}