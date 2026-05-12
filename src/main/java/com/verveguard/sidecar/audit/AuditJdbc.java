package com.verveguard.sidecar.audit;

import com.verveguard.sidecar.Entity.TransactionLog;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * This handles the saving of the Transaction Logs
 */


@Repository
@RequiredArgsConstructor
public class AuditJdbc {

    private final JdbcTemplate jdbcTemplate;

    @CacheEvict(value = "flaggedLogs", allEntries = true)
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

    @Cacheable(value = "flaggedLogs", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TransactionLog> getFlaggedLogs(Pageable pageable) {
        String dataSQL = """
            SELECT * FROM TransactionLog
            WHERE status != 'APPROVED'
            ORDER BY createdAt DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;

        String countSQL = """
            SELECT COUNT(*) FROM TransactionLog
            WHERE status != 'APPROVED'
            """;

        List<TransactionLog> logs = jdbcTemplate.query(
                dataSQL,
                (rs, rowNum) -> TransactionLog.builder()
                        .cardNumber(rs.getString("cardNumber"))
                        .amount(rs.getBigDecimal("amount"))
                        .merchantId(rs.getString("merchantId"))
                        .ipAddress(rs.getString("ipAddress"))
                        .status(rs.getString("status"))
                        .executionTimeMs(rs.getLong("executionTimeMs"))
                        .build(),
                pageable.getOffset(),
                pageable.getPageSize()
        );

        long total = jdbcTemplate.queryForObject(countSQL, Long.class);

        return new PageImpl<>(logs, pageable, total);
    }
}