package com.verveguard.sidecar.fraud;

import com.verveguard.sidecar.ingestion.TransactionRequestDto;
import com.verveguard.sidecar.merchant.BlacklistedMerchantRepository;
import com.verveguard.sidecar.ratelimit.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudService {

    private final RateLimiterService rateLimiterService;
    private final BlacklistedMerchantRepository merchantRepository;

    /**
     * Evaluates the transaction and returns its final status.
     * Note: If the rate limit is exceeded, rateLimiterService throws an exception,
     * instantly halting this method before it ever touches the database.
     */
    public String evaluateTransaction(TransactionRequestDto request) {

        // 1. The Bouncer (Check IP Address)
        rateLimiterService.checkLimit(request.getIpAddress());

        // 2. The Background Check (Check Merchant ID via Cache/JPA)
        log.info("Checking merchant status for: {}", request.getMerchantId());
        boolean isBlacklisted = merchantRepository.existsByMerchantId(request.getMerchantId());

        if (isBlacklisted) {
            log.warn("Transaction blocked. Merchant {} is blacklisted.", request.getMerchantId());
            return "BLOCKED_FRAUD";
        }

        // 3. The Verdict
        return "APPROVED";
    }
}
