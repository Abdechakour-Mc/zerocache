package com.venus.zerocache.batching;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BestFitSelector implements BatchSelector {

    @Override
    public List<String> pickBatch(List<PendingRequest> pending, int budget) {

        Objects.requireNonNull(pending, "pending must not be null");

        List<PendingRequest> sortedPending = pending.stream()
                            .sorted(Comparator.comparing(PendingRequest::tokens))
                            .toList();

        int leftIdx = 0;
        int rightIdx = pending.size() - 1;
        List<String> best = List.of();
        int bestSum = -1;
        
        while (leftIdx < rightIdx) {
            PendingRequest a = sortedPending.get(leftIdx);
            PendingRequest b = sortedPending.get(rightIdx);
            int sum = a.tokens() + b.tokens();
            
            if (sum <= budget) {
                if (sum > bestSum) {
                    bestSum = sum;
                    best = List.of(a.id(), b.id());
                }
                leftIdx += 1;
            } else {
                rightIdx -= 1;
            }
        }

        return best;
    }
}
