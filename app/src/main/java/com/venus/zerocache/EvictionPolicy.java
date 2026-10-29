package com.venus.zerocache;

public interface EvictionPolicy<K> {
    void onKeyAdded(K key);

    void onKeyRemoved(K key);

    K selectVictim();

    default void onKeyAccessed(K key) {
    };
}
