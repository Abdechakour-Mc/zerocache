package com.venus.zerocache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InMemoryCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, V> store = new HashMap<>();
    private final EvictionPolicy<K> policy;

    public InMemoryCache(int capacity, EvictionPolicy<K> policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");

        if (store.containsKey(key)) {
            store.put(key, value);
            policy.onKeyAccessed(key);
            return;
        }

        if (store.size() == capacity) {
            K victim = policy.selectVictim();
            store.remove(victim);
            policy.onKeyRemoved(victim);
        }

        store.put(key, value);
        policy.onKeyAdded(key);
    }

    @Override
    public V get(K key) {
        if (store.containsKey(key)) {
            policy.onKeyAccessed(key);
        }
        return store.get(key);
    }

    @Override
    public void remove(K key) {
        if (store.containsKey(key)) {
            store.remove(key);
            policy.onKeyRemoved(key);
        }
    }

    @Override
    public int size() {
        return store.size();
    }

}
