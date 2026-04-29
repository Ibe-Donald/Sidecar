package com.verveguard.sidecar.audit;

import com.verveguard.sidecar.Entity.TransactionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This handles the saving of the Transaction Logs
 */


@Repository
@RequiredArgsConstructor
public class AuditJdbc {

    private final JdbcTemplate jdbcTemplate;


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
