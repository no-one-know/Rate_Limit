package org.example.algorithms;

import org.example.RateLimitResult;

public interface RateLimitAlgorithm {
    RateLimitResult execute(String redisKey, int capacity, int rateOrTimeWindow, long timestampSeconds);
}
