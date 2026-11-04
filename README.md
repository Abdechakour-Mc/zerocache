# ZeroCache

A key-value cache server in Java, built to practice clean low-level and high-level design (LLD & HLD).
The goal is a distributed cache with pluggable eviction policies and pluggable network transports (TCP first, RDMA later).

## Status
Early stage: a single-node, in-memory.

- Generic "Cache<K, V>" interface with an in-memory implementation
- Swappable eviction policies (Fifo, Lifo). Adding a new policy requires no changes to the cache class.
- Unit tests with JUnit 5 


## Run the tests
Using the following cmd: `./gradlew test`

Requires Java 21.

## Roadmap
- [ ] More eviction policies (LRU, LFU, TTL)
- [ ] TCP server with a simple text protocol
- [ ] REST admin and stats API (Spring Boot)
- [ ] Sharding and replication
- [ ] RDMA transport
