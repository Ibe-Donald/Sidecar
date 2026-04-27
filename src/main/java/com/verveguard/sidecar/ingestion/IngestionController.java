package com.verveguard.sidecar.ingestion;


import com.verveguard.sidecar.fraud.FraudService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class IngestionController {

    // Injecting the "Brain" of the application
    private final FraudService fraudService;


     // The main entry point for the POS terminals.
     // We use @Valid to enforce the @NotBlank and @NotNull rules from our DTO.

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyTransaction(
            @Valid @RequestBody TransactionRequestDto incomingRequest,
            HttpServletRequest httpRequest) {

        // 1. Extract the true network IP address to prevent spoofing
        String realIpAddress = extractIpAddress(httpRequest);

        // 2. Forcefully override the IP in the JSON body
        incomingRequest.setIpAddress(realIpAddress);

        log.info("Received transaction request for merchant: {} from IP: {}",
                incomingRequest.getMerchantId(), realIpAddress);

        // 3. Hand the DTO over to the FraudService.
        // Because of our PerformanceAspect, the stopwatch automatically starts here!
        String status = fraudService.evaluateTransaction(incomingRequest);

        // 4. Format the final JSON response
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("merchantId", incomingRequest.getMerchantId());

        // 5. Return the correct HTTP Status Code based on the result
        if ("APPROVED".equals(status)) {
            // 200 OK
            return ResponseEntity.ok(response);
        } else {
            // 403 Forbidden (Blocked by Fraud)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }


     // Helper method to accurately extract the client IP.

    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
