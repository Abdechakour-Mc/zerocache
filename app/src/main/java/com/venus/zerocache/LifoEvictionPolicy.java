package com.venus.zerocache;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LifoEvictionPolicy
 */
public class LifoEvictionPolicy<K> implements EvictionPolicy<K> {

    private final Deque<K> insertionOrder = new ArrayDeque<>();

    @Override
    public void onKeyAdded(K key) {
        insertionOrder.addFirst(key);
    }

    @Override
    public void onKeyRemoved(K key) {
        insertionOrder.remove(key);
    }

    @Override
    public K selectVictim() {
        return insertionOrder.getFirst();
    }
}