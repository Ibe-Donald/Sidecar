package com.verveguard.sidecar.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedResponseDto {
    private UUID id;
    private String merchantId;
    private String reason;
    private LocalDateTime createdAt;
}