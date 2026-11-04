package com.venus.zerocache.batching;

import java.util.Objects;

public record PendingRequest(String id, int tokens) {
    public PendingRequest {
        Objects.requireNonNull(id, "id must not be null");

        if (tokens < 0) {
            throw new IllegalArgumentException("tokens must be >= 0");
        }
    }
}
