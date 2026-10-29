package com.venus.zerocache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class InMemoryCacheTest {

    @Test
    void evictsOldestWhenFull() {
        Cache<String, Integer> cache = new InMemoryCache<>(2, new FifoEvictionPolicy<>());

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        assertNull(cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(2, cache.size());

    }

    @Test
    void lifoEvictsNewestWhenFull() {
        Cache<String, Integer> cache = new InMemoryCache<>(2, new LifoEvictionPolicy<>());

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3); // full: LIFO evicts B, the newest key before C

        assertEquals(1, cache.get("A"));
        assertNull(cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(2, cache.size());
    }
}