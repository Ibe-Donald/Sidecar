package com.verveguard.sidecar.admin;


import com.verveguard.sidecar.audit.AuditJdbcRepository;
import com.verveguard.sidecar.audit.TransactionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuditJdbcRepository auditRepository;

    // NEW: We inject the Badge Maker so we can generate testing tokens
    private final JwtService jwtService;

    /**
     * Requirement 4: JWT-protected GET endpoint to view flagged attempts.
     * Only users with a valid token will ever reach this code.
     */
    @GetMapping("/flagged-attempts")
    public ResponseEntity<List<TransactionLog>> getFlaggedTransactions() {

        // Fetch all transactions that were not approved
        List<TransactionLog> flaggedLogs = auditRepository.getFlaggedLogs();

        return ResponseEntity.ok(flaggedLogs);
    }

    /**
     * TEMPORARY BACKDOOR FOR POSTMAN TESTING ONLY.
     * This generates a valid JWT so you can test the flagged-attempts endpoint.
     * In a real production application, this would be a proper POST /login endpoint
     * that checks a username and password against a database before issuing the token.
     */
    @GetMapping("/generate-token")
    public ResponseEntity<String> getTestToken() {
        // Generate a 1-hour digital badge for a dummy admin user
        String token = jwtService.generateToken("superadmin");
        return ResponseEntity.ok(token);
    }
}