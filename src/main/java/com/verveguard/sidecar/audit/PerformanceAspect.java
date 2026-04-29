package com.verveguard.sidecar.audit;



import com.verveguard.sidecar.Dto.TransactionRequestDto;
import com.verveguard.sidecar.Entity.TransactionLog;
import com.verveguard.sidecar.exception.RateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Measures the execution time for each transaction
 * Captures the final status
 * Persist audit log to database
 */

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PerformanceAspect {

    private final AuditJdbc auditJdbc;


     // @Around annotation to Intercept any call to the evaluateTransaction method

    @Around("execution(* com.verveguard.sidecar.Service.FraudService.evaluateTransaction(..))")
    public Object auditTransactionPerformance(ProceedingJoinPoint joinPoint) throws Throwable {


        long startTime = System.currentTimeMillis();
        String finalStatus;

        Object[] args = joinPoint.getArgs();
        TransactionRequestDto request = (TransactionRequestDto) args[0];

        try {
            finalStatus = (String) joinPoint.proceed();

        } catch (RateLimitException ex) {

            finalStatus = "BLOCKED_RATE_LIMIT";
            saveAuditLog(request, finalStatus, startTime);
            throw ex;

        } catch (Exception ex) {
            finalStatus = "SYSTEM_ERROR";
            saveAuditLog(request, finalStatus, startTime);
            throw ex;
        }

        // Log transaction regardless of if it is successful or not
        saveAuditLog(request, finalStatus, startTime);

        return finalStatus;
    }

    // Helper method to build and save the high-speed log.

    private void saveAuditLog(TransactionRequestDto request, String status, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;

        log.info("Transaction processed in {} ms with status: {}", executionTime, status);

        TransactionLog logEntry = TransactionLog.builder()
                .cardNumber(request.getCardNumber())
                .amount(request.getAmount())
                .merchantId(request.getMerchantId())
                .ipAddress(request.getIpAddress())
                .status(status)
                .executionTimeMs(executionTime)
                .build();

        auditJdbc.saveLog(logEntry);
    }
}
