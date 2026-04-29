package com.verveguard.sidecar.Service;

import com.verveguard.sidecar.exception.RateLimitException;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // The maximum requests allowed per minute
    private static final int MAX_REQUESTS = 5;
    // 60 seconds in milliseconds
    private static final long TIME_WINDOW_MS = 60000;

    // Maps an IP Address to a list of timestamps
    private final Map<String, Deque<Long>> requestCounts = new ConcurrentHashMap<>();


    public void checkLimit(String ipAddress) {
        long currentTime = System.currentTimeMillis();

        requestCounts.putIfAbsent(ipAddress, new LinkedList<>());
        Deque<Long> timestamps = requestCounts.get(ipAddress);

        // Synchronization is needed because multiple HTTP requests from the
        // same IP could hit this exact block of code at the exact same millisecond.
        synchronized (timestamps) {

            while (!timestamps.isEmpty() && currentTime - timestamps.peekFirst() > TIME_WINDOW_MS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                throw new RateLimitException("Too many requests from your IP: " + ipAddress);
            }

            timestamps.addLast(currentTime);
        }
    }
}