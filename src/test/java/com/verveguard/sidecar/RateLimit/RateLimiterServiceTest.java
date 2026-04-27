package com.verveguard.sidecar.RateLimit;


import com.verveguard.sidecar.ratelimit.RateLimitException;
import com.verveguard.sidecar.ratelimit.RateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;
    private final String testIp = "192.168.1.100";


     // @BeforeEach ensures we get a brand new, empty RateLimiter
     // before every single test runs.

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    void whenFirstRequest_thenDoNotThrowException() {
        // ACT & ASSERT
        // The most common way to assert that something succeeds is using assertDoesNotThrow.
        // If the limit check fails, it throws an error and fails the test.
        assertDoesNotThrow(() -> {
            rateLimiterService.checkLimit(testIp);
        });
    }

    @Test
    void whenExactlyFiveRequests_thenDoNotThrowException() {
        // ACT & ASSERT
        assertDoesNotThrow(() -> {
            // Simulate 5 rapid-fire requests from the exact same IP
            for (int i = 0; i < 5; i++) {
                rateLimiterService.checkLimit(testIp);
            }
        });
    }

    @Test
    void whenSixthRequestInOneMinute_thenThrowRateLimitException() {
        // ARRANGE: Max out the limit first (5 requests)
        for (int i = 0; i < 5; i++) {
            rateLimiterService.checkLimit(testIp);
        }

        // ACT & ASSERT: The 6th request MUST trigger the Bouncer
        RateLimitException thrown = assertThrows(
                RateLimitException.class,
                () -> rateLimiterService.checkLimit(testIp)
        );

        // Verify the error message is exactly what we expect the GlobalExceptionHandler to see
        assertTrue(thrown.getMessage().contains("Too many requests from IP: " + testIp));
    }

    @Test
    void whenDifferentIpsRequest_thenTrackSeparately() {
        // ARRANGE
        String ip1 = "10.0.0.1";
        String ip2 = "10.0.0.2";

        // Max out IP 1
        for (int i = 0; i < 5; i++) {
            rateLimiterService.checkLimit(ip1);
        }

        // ACT & ASSERT
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