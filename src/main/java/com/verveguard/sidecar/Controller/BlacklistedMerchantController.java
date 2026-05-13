package com.verveguard.sidecar.Controller;

import com.verveguard.sidecar.Dto.BlacklistedResponseDto;
import com.verveguard.sidecar.Service.BlacklistedMerchantService; // Replace with your actual service package
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blacklisted-merchants")
public class BlacklistedMerchantController {

    private final BlacklistedMerchantService merchantService;

    public BlacklistedMerchantController(BlacklistedMerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping
    public ResponseEntity<Page<BlacklistedResponseDto>> getBlacklistedMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BlacklistedResponseDto> response = merchantService.fetchUsers(page, size);
        return ResponseEntity.ok(response);
    }
}