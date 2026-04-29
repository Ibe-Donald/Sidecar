package com.verveguard.sidecar.fraud;


import com.verveguard.sidecar.Service.FraudService;
import com.verveguard.sidecar.Dto.TransactionRequestDto;
import com.verveguard.sidecar.Repository.BlacklistedMerchantRepository;
import com.verveguard.sidecar.exception.RateLimitException;
import com.verveguard.sidecar.Service.RateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FraudServiceTest {

    // 1. Create the Dummies (Mocks)
    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private BlacklistedMerchantRepository merchantRepository;

    // 2. Inject the Dummies into the real service we are testing
    @InjectMocks
    private FraudService fraudService;

    private TransactionRequestDto testRequest;


     // @BeforeEach runs before every single @Test.
    // It ensures we start with a fresh, clean payload every time.

    @BeforeEach
    void setUp() {
        testRequest = new TransactionRequestDto();
        testRequest.setCardNumber("1234-5678-9012-3456");
        testRequest.setAmount(new BigDecimal("50000.00"));
        testRequest.setMerchantId("MERCH-999");
        testRequest.setIpAddress("192.168.1.5");
    }

    @Test
    void whenMerchantIsClean_thenApproveTransaction() {
        // ARRANGE: Train the dummies
        // Tell the rate limiter to do nothing (allow it), and the DB to say the merchant is NOT blacklisted
        doNothing().when(rateLimiterService).checkLimit(anyString());
        when(merchantRepository.existsByMerchantId("MERCH-999")).thenReturn(false);

        // ACT: Run the real method
        String result = fraudService.evaluateTransaction(testRequest);

        // ASSERT: Prove it worked
        assertEquals("APPROVED", result);

        // Bonus: Prove the service actually talked to the dummies exactly one time
        verify(rateLimiterService, times(1)).checkLimit("192.168.1.5");
        verify(merchantRepository, times(1)).existsByMerchantId("MERCH-999");
    }

    @Test
    void whenMerchantIsBlacklisted_thenBlockTransaction() {
        // ARRANGE
        doNothing().when(rateLimiterService).checkLimit(anyString());
        when(merchantRepository.existsByMerchantId("MERCH-999")).thenReturn(true);

        // ACT
        String result = fraudService.evaluateTransaction(testRequest);

        // ASSERT
        assertEquals("BLOCKED_FRAUD", result);
        verify(merchantRepository, times(1)).existsByMerchantId("MERCH-999");
    }

    @Test
    void whenRateLimitExceeded_thenThrowException() {
        // ARRANGE
        // Train the bouncer to violently throw an error when this IP shows up
        doThrow(new RateLimitException("Too many requests"))
                .when(rateLimiterService).checkLimit("192.168.1.5");

        // ACT & ASSERT
        // We assert that calling the service actively triggers the Exception
        assertThrows(RateLimitException.class, () -> {
            fraudService.evaluateTransaction(testRequest);
        });

        // CRUCIAL SECURITY CHECK: Prove the "Fail-Fast" mechanism works.
        // Because the bouncer threw them out, the database should NEVER have been queried.
        verify(merchantRepository, never()).existsByMerchantId(anyString());
    }
}
