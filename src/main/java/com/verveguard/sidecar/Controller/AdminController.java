package com.verveguard.sidecar.Controller;


import com.verveguard.sidecar.Dto.AdminRequestDto;
import com.verveguard.sidecar.Dto.AdminResponseDto;
import com.verveguard.sidecar.Dto.LoginRequestDto;
import com.verveguard.sidecar.Dto.LoginResponseDto;
import com.verveguard.sidecar.Entity.Admin;
import com.verveguard.sidecar.Service.AdminService;
import com.verveguard.sidecar.Service.JwtService;
import com.verveguard.sidecar.audit.AuditJdbc;
import com.verveguard.sidecar.Entity.TransactionLog;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuditJdbc auditRepository;
    private final JwtService jwtService;
    private final AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<AdminResponseDto> createAdmin(@RequestBody @Valid AdminRequestDto dto){

        AdminResponseDto admin = adminService.createAdmin(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(admin);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> adminLogin(@RequestBody @Valid LoginRequestDto dto){
        LoginResponseDto login = adminService.adminLogin(dto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(login);
    }

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
//    @GetMapping("/generate-token")
//    public ResponseEntity<String> getTestToken() {
//        String token = jwtService.generateToken("superadmin");
//        return ResponseEntity.ok(token);
//    }
}