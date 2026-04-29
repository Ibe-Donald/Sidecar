package com.verveguard.sidecar.Entity;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransactionLog {
    private String cardNumber;
    private BigDecimal amount;
    private String merchantId;
    private String ipAddress;
    private String status;
    private long executionTimeMs;
}
