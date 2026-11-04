package com.venus.zerocache.batching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ExactFitSelectorTest {

    private final BatchSelector selector = new ExactFitSelector();

    @Test
    void findsPairThatFillsBudget() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 512),
                new PendingRequest("b", 2048),
                new PendingRequest("c", 1024),
                new PendingRequest("d", 4096));

        List<String> result = selector.pickBatch(pending, 3072);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("b", "c")));
    }

    @Test
    void findsPairWhenFirstRequestIsPartOfIt() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 300),
                new PendingRequest("b", 128),
                new PendingRequest("c", 900),
                new PendingRequest("d", 700));

        List<String> result = selector.pickBatch(pending, 1000);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("a", "d")));
    }

    @Test
    void pairsTwoDifferentRequestsWithEqualTokens() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 256),
                new PendingRequest("b", 256));

        List<String> result = selector.pickBatch(pending, 512);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("a", "b")));
    }

    @Test
    void returnsEmptyListWhenNoPairFits() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 100),
                new PendingRequest("b", 200));

        assertTrue(selector.pickBatch(pending, 999).isEmpty());
    }

    @Test
    void returnsEmptyListForEmptyInput() {
        assertTrue(selector.pickBatch(List.of(), 100).isEmpty());
    }

    @Test
    void singleRequestCannotPairWithItself() {
        List<PendingRequest> pending = List.of(new PendingRequest("a", 500));

        assertTrue(selector.pickBatch(pending, 1000).isEmpty());
    }

    @Test
    void requestEqualToHalfTheBudgetDoesNotPairWithItself() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 500),
                new PendingRequest("b", 300));

        assertTrue(selector.pickBatch(pending, 1000).isEmpty());
    }

    @Test
    void anyValidPairIsAcceptedWhenSeveralExist() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 1),
                new PendingRequest("b", 2),
                new PendingRequest("c", 3),
                new PendingRequest("d", 4));

        List<String> result = selector.pickBatch(pending, 5);

        assertEquals(2, result.size());
        assertNotEquals(result.get(0), result.get(1));
        assertEquals(5, tokensOf(pending, result.get(0)) + tokensOf(pending, result.get(1)));
    }

    @Test
    void handlesZeroTokenRequest() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 0),
                new PendingRequest("b", 700));

        List<String> result = selector.pickBatch(pending, 700);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("a", "b")));
    }

    @Test
    void findsPairAtTheEndOfTheList() {
        List<PendingRequest> pending = List.of(
                new PendingRequest("a", 10),
                new PendingRequest("b", 20),
                new PendingRequest("c", 30),
                new PendingRequest("d", 400),
                new PendingRequest("e", 600));

        List<String> result = selector.pickBatch(pending, 1000);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of("d", "e")));
    }

    // Looks up a request's token count by id, so tests can check a pair's sum.
    private static int tokensOf(List<PendingRequest> pending, String id) {
        for (PendingRequest request : pending) {
            if (request.id().equals(id)) {
                return request.tokens();
            }
        }
        throw new IllegalArgumentException("unknown id: " + id);
    }
}