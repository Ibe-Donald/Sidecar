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
        testRequest.setMerchantId("M006");
        testRequest.setIpAddress("192.168.1.5");
    }

    @Test
    void whenMerchantIsClean_thenApproveTransaction() {

        doNothing().when(rateLimiterService).checkLimit(anyString());
        when(merchantRepository.existsByMerchantId("M006")).thenReturn(false);


        String result = fraudService.evaluateTransaction(testRequest);


        assertEquals("APPROVED", result);


        verify(rateLimiterService, times(1)).checkLimit("192.168.1.5");
        verify(merchantRepository, times(1)).existsByMerchantId("M006");
    }

    @Test
    void whenMerchantIsBlacklisted_thenBlockTransaction() {

        doNothing().when(rateLimiterService).checkLimit(anyString());
        when(merchantRepository.existsByMerchantId("M006")).thenReturn(true);


        String result = fraudService.evaluateTransaction(testRequest);


        assertEquals("BLOCKED_FRAUD", result);
        verify(merchantRepository, times(1)).existsByMerchantId("M006");
    }

    @Test
    void whenRateLimitExceeded_thenThrowException() {

        doThrow(new RateLimitException("Too many requests"))
                .when(rateLimiterService).checkLimit("192.168.1.5");

        assertThrows(RateLimitException.class, () -> {
            fraudService.evaluateTransaction(testRequest);
        });

        verify(merchantRepository, never()).existsByMerchantId(anyString());
    }
}
