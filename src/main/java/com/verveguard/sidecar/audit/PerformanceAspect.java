package com.verveguard.sidecar.audit;



import com.verveguard.sidecar.ingestion.TransactionRequestDto;
import com.verveguard.sidecar.ratelimit.RateLimitException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PerformanceAspect {

    private final AuditJdbcRepository auditJdbcRepository;


     // The @Around annotation tells Spring: "Intercept any call to the evaluateTransaction method."

    @Around("execution(* com.verveguard.sidecar.fraud.FraudService.evaluateTransaction(..))")
    public Object auditTransactionPerformance(ProceedingJoinPoint joinPoint) throws Throwable {

        // 1. Start the stopwatch
        long startTime = System.currentTimeMillis();
        String finalStatus;

        // Extract the incoming payload (The Courier's Slip) from the method arguments
        Object[] args = joinPoint.getArgs();
        TransactionRequestDto request = (TransactionRequestDto) args[0];

        try {
            // 2. Let the FraudService do its job (The 'proceed' command)
            finalStatus = (String) joinPoint.proceed();

        } catch (RateLimitException ex) {
            // If the bouncer kicks them out, we catch it to log the failure, then throw it back
            finalStatus = "BLOCKED_RATE_LIMIT";
            saveAuditLog(request, finalStatus, startTime);
            throw ex;

        } catch (Exception ex) {
            finalStatus = "SYSTEM_ERROR";
            saveAuditLog(request, finalStatus, startTime);
            throw ex;
        }

        // 3. If it succeeded (Approved or Blocked_Fraud), log it
        saveAuditLog(request, finalStatus, startTime);

        // 4. Return the result back to the Controller
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

        // raw JDBC insert
        auditJdbcRepository.saveLog(logEntry);
    }
}
