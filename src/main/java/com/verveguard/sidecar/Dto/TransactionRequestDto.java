package com.verveguard.sidecar.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount cannot be zero or negative")
    private BigDecimal amount;

    @NotNull(message = "Merchant ID is required")
    private String merchantId;

    private String ipAddress;
}
