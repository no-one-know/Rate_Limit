package org.example;

import org.example.properties.entities.SecondaryRateLimit;

public record ResolvedEndpointRateLimit(
        RateLimitExecutionConfig primary,
        RateLimitExecutionConfig secondary,
        SecondaryRateLimit secondaryMeta) {}
