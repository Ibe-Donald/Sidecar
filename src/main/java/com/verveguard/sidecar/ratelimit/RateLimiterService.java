package com.verveguard.sidecar.ratelimit;

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

    // The In-Memory Datastore: Maps an IP Address to a list of timestamps
    private final Map<String, Deque<Long>> requestCounts = new ConcurrentHashMap<>();


     // Checks if the IP has exceeded the limit. Throws an exception if it has.

    public void checkLimit(String ipAddress) {
        long currentTime = System.currentTimeMillis();

        // If this is the first time we are seeing this IP, give it an empty list
        requestCounts.putIfAbsent(ipAddress, new LinkedList<>());
        Deque<Long> timestamps = requestCounts.get(ipAddress);

        // Synchronization is needed here because multiple HTTP requests from the
        // same IP could hit this exact block of code at the exact same millisecond.
        synchronized (timestamps) {

            // 1. Remove old timestamps that fall outside our 60-second window
            while (!timestamps.isEmpty() && currentTime - timestamps.peekFirst() > TIME_WINDOW_MS) {
                timestamps.pollFirst();
            }

            // 2. Check if the remaining valid timestamps equal or exceed our limit
            if (timestamps.size() >= MAX_REQUESTS) {
                throw new RateLimitException("Too many requests from IP: " + ipAddress);
            }

            // 3. If they are safe, record this current request's timestamp
            timestamps.addLast(currentTime);
        }
    }
}