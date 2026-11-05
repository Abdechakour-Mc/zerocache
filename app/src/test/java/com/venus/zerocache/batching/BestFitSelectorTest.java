package com.venus.zerocache.batching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

class BestFitSelectorTest {

    private final BatchSelector selector = new BestFitSelector();

    private static final List<PendingRequest> SAMPLE = List.of(
            new PendingRequest("a", 512),
            new PendingRequest("b", 2048),
            new PendingRequest("c", 1024),
            new PendingRequest("d", 4096));

    @Test
    void picksLargestSumUnderBudget() {
        List<String> result = selector.pickBatch(SAMPLE, 3000);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("a", "b")));   // 2560
    }

    @Test
    void picksExactFitWhenOneExists() {
        List<String> result = selector.pickBatch(SAMPLE, 3072);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("b", "c")));   // 3072
    }

    @Test
    void prefersBiggerSumOverEarlierPair() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 300),
                new PendingRequest("b", 128),
                new PendingRequest("c", 900),
                new PendingRequest("d", 700));

        List<String> result = selector.pickBatch(pending, 1050);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("b", "c")));   // 1028 beats 300 + 700 = 1000
    }

    @Test
    void pairsTwoDifferentRequestsWithEqualTokens() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 256),
                new PendingRequest("b", 256));

        List<String> result = selector.pickBatch(pending, 600);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("a", "b")));
    }

    @Test
    void returnsEmptyListWhenEveryPairIsTooBig() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 100),
                new PendingRequest("b", 200));

        assertTrue(selector.pickBatch(pending, 250).isEmpty());
    }

    @Test
    void doesNotSkipPairsOnTiedSums() {
        // sorted tokens [1, 1, 2, 4], budget 6: the best is 2 + 4, not 1 + 4
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 1),
                new PendingRequest("b", 1),
                new PendingRequest("c", 2),
                new PendingRequest("d", 4));

        List<String> result = selector.pickBatch(pending, 6);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("c", "d")));
    }

    @Test
    void returnsEmptyListForEmptyInput() {
        assertTrue(selector.pickBatch(List.of(), 100).isEmpty());
    }

    @Test
    void singleRequestCannotPairWithItself() {
        List<PendingRequest> pending = List.of(new PendingRequest("a", 10));

        assertTrue(selector.pickBatch(pending, 1000).isEmpty());
    }

    @Test
    void returnsEmptyListForNegativeBudget() {
        assertTrue(selector.pickBatch(SAMPLE, -1).isEmpty());
    }

    @Test
    void zeroBudgetMatchesOnlyTwoZeroTokenRequests() {
        List<PendingRequest> zeros = List.of(
                new PendingRequest("a", 0),
                new PendingRequest("b", 0),
                new PendingRequest("c", 5));
        List<PendingRequest> noZeros = List.of(
                new PendingRequest("a", 1),
                new PendingRequest("b", 2));

        List<String> result = selector.pickBatch(zeros, 0);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("a", "b")));
        assertTrue(selector.pickBatch(noZeros, 0).isEmpty());
    }

    @Test
    void doesNotModifyTheCallersList() {
        // ArrayList on purpose: it is mutable, so an in-place sort would change it
        List<PendingRequest> pending = new ArrayList<>(List.of(
                new PendingRequest("a", 300),
                new PendingRequest("b", 100),
                new PendingRequest("c", 200)));
        List<PendingRequest> snapshot = new ArrayList<>(pending);

        selector.pickBatch(pending, 350);

        assertEquals(snapshot, pending);
    }

    @Test
    void nullListThrows() {
        assertThrows(NullPointerException.class, () -> selector.pickBatch(null, 100));
    }

    @Test
    void matchesBruteForceOnRandomInputs() {
        Random random = new Random(42);   // fixed seed: failures are reproducible

        for (int round = 0; round < 500; round++) {
            List<PendingRequest> pending = randomRequests(random, random.nextInt(30));
            int budget = random.nextInt(250);

            int expectedBest = bruteForceBestSum(pending, budget);
            List<String> result = selector.pickBatch(pending, budget);

            if (expectedBest == -1) {
                assertTrue(result.isEmpty(), "expected no pair, budget " + budget + ", input " + pending);
            } else {
                assertEquals(2, result.size(), "budget " + budget + ", input " + pending);
                assertNotEquals(result.get(0), result.get(1));
                int sum = tokensOf(pending, result.get(0)) + tokensOf(pending, result.get(1));
                assertEquals(expectedBest, sum, "budget " + budget + ", input " + pending);
            }
        }
    }

    // ---- helpers ----

    private static List<PendingRequest> randomRequests(Random random, int count) {
        List<PendingRequest> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            requests.add(new PendingRequest("r" + i, random.nextInt(101)));
        }
        return requests;
    }

    // The slow, obviously-correct version: check every pair. Returns -1 if none fits.
    private static int bruteForceBestSum(List<PendingRequest> pending, int budget) {
        int best = -1;
        for (int i = 0; i < pending.size(); i++) {
            for (int j = i + 1; j < pending.size(); j++) {
                int sum = pending.get(i).tokens() + pending.get(j).tokens();
                if (sum <= budget && sum > best) {
                    best = sum;
                }
            }
        }
        return best;
    }

    private static int tokensOf(List<PendingRequest> pending, String id) {
        for (PendingRequest request : pending) {
            if (request.id().equals(id)) {
                return request.tokens();
            }
        }
        throw new IllegalArgumentException("unknown id: " + id);
    }
}