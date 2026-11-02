
package com.ga.parking.concurrency;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SpotLockManager {

    private final ConcurrentHashMap<String, ReentrantLock> locks =
            new ConcurrentHashMap<>();

    public ReentrantLock getLock(String spotId) {
        return locks.computeIfAbsent(
                spotId,
                id -> new ReentrantLock()
        );
    }
}
