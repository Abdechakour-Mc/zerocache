package com.venus.zerocache.batching;

import java.util.List;

public interface BatchSelector {
    List<String> pickBatch( List<PendingRequest> pending, int budget);
}
