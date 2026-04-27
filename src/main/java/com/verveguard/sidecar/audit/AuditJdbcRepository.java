package com.verveguard.sidecar.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditJdbcRepository {

    private final JdbcTemplate jdbcTemplate;


     // Executes a raw, high-speed SQL insert.
     // We do not insert the 'id' or 'created_at' because the database
     // handles those automatically with DEFAULT NEWID() and DEFAULT GETDATE().

    public void saveLog(TransactionLog log) {
        String sql = """
            INSERT INTO TransactionLog 
            (cardNumber, amount, merchantId, ipAddress, status, executionTimeMs) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
                log.getCardNumber(),
                log.getAmount(),
                log.getMerchantId(),
                log.getIpAddress(),
                log.getStatus(),
                log.getExecutionTimeMs()
        );
    }

    // Add this inside AuditJdbcRepository.java

    public List<TransactionLog> getFlaggedLogs() {
        String sql = "SELECT * FROM TransactionLog WHERE status != 'APPROVED' ORDER BY createdAt DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> TransactionLog.builder()
                .cardNumber(rs.getString("cardNumber"))
                .amount(rs.getBigDecimal("amount"))
                .merchantId(rs.getString("merchantId"))
                .ipAddress(rs.getString("ipAddress"))
                .status(rs.getString("status"))
                .executionTimeMs(rs.getLong("executionTimeMs"))
                .build()
        );
    }
}
