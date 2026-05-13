package com.verveguard.sidecar.Controller;


import com.verveguard.sidecar.Service.FraudService;
import com.verveguard.sidecar.Dto.TransactionRequestDto;
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
public class FraudController {

    private final FraudService fraudService;


    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyTransaction(
            @Valid @RequestBody TransactionRequestDto incomingRequest) {

        log.info("Received transaction request for merchant: {} from IP: {}",
                incomingRequest.getMerchantId(), incomingRequest.getIpAddress());

        String status = fraudService.evaluateTransaction(incomingRequest);

        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("merchantId", incomingRequest.getMerchantId());

        if ("APPROVED".equals(status)) {

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

}
