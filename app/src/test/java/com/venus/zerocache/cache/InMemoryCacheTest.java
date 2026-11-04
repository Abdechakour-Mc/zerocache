package com.venus.zerocache.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.venus.zerocache.cache.Cache;
import com.venus.zerocache.cache.InMemoryCache;
import com.venus.zerocache.eviction.FifoEvictionPolicy;
import com.venus.zerocache.eviction.LifoEvictionPolicy;

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

    @Test
    void overwritingExistingKeyDoesNotEvict() {
        Cache<String, Integer> cache = new InMemoryCache<>(2, new FifoEvictionPolicy<>());

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("A", 3);               // overwrite, not a new entry

        assertEquals(3, cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(2, cache.size());
    }

    @Test
    void removeFreesSpaceSoNextPutDoesNotEvict() {
        Cache<String, Integer> cache = new InMemoryCache<>(2, new FifoEvictionPolicy<>());

        cache.put("A", 1);
        cache.put("B", 2);
        cache.remove("A");
        cache.put("C", 4);

        assertEquals(2, cache.get("B"));
        assertEquals(4, cache.get("C"));
        assertEquals(2, cache.size());
    }

    @Test
    void getMissingKeyReturnsNull() {
        Cache<String, Integer> cache = new InMemoryCache<>(2, new FifoEvictionPolicy<>());

        assertNull(cache.get("X"));
    }

    @Test 
    void zeroCapacityThrows() {
        assertThrows(IllegalArgumentException.class,() -> new InMemoryCache<String, Integer>(0, new LifoEvictionPolicy<>()));
    }

    @Test
    void nullKeyThrows() {
        Cache<String, Integer> cache = new InMemoryCache<>(2, new LifoEvictionPolicy<>());

        assertThrows(NullPointerException.class, () -> cache.put(null, 1));
    }

}