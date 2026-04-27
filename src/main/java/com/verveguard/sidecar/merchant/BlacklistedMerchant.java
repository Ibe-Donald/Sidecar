package com.verveguard.sidecar.merchant;

import com.sun.jdi.PrimitiveValue;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "BlacklistedMerchant")
public class BlacklistedMerchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String reason;

    private LocalDateTime createdAt;
}
