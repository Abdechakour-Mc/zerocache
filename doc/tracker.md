# Zerocache: Learning Tracker

> **For Claude:** Read this file first. It replaces chat history. Follow the "Working agreement", continue from "Current status", and update the "Progress log" at the end of each session.

---

## 1. Goal
Refresh Java and build solid LLD/HLD skills through ONE small project that grows with the course. The final project goes on my CV and shows SOLID, clean code and system-design understanding. It should also cover the Java syntax needed for LeetCode (collections, strings, arrays, etc.).

## 2. Project: Zerocache
A pluggable-eviction, pluggable-transport cache server in Java.
- Starts as an in-memory cache with swappable eviction policies (FIFO, LIFO done; LRU, LFU, TTL later).
- Grows a network server (TCP first), then Spring Boot APIs, persistence, deployment, sharding/replication.
- Stretch goal: an RDMA transport behind the same `Transport` interface (I'm interested in RDMA and networking).
- Pitch for CV: "Pluggable-eviction, pluggable-transport cache server in Java with a TCP baseline and an RDMA transport."

## 3. Working agreement
- **Keep replies short. No fluff.**
- **Claude gives tasks, not code.** Each day: a problem statement with requirements and constraints (no solution), docs/topics to look up, hints only if I ask.
- I write the code, push it to GitHub, and paste/link it. Claude reviews for SOLID violations, clean code and Java idioms, and we fix it together.
- If I'm stuck after trying, I say so and Claude may give the answer or a partial one. My Java syntax is rusty, so guided typing of small pieces is OK. Design decisions (which classes, what interfaces) should be mine first.
- "Naive first, then refactor" is allowed: write a simple version, then apply the principle. Refactor in small steps with tests staying green, don't rewrite.
- Small steps. Not so much that I get demotivated, not so little that I don't learn.
- One small LeetCode-style exercise per day that uses the same Java features I just touched.
- I report what the course covered each day, and the plan adapts to it.
- Claude should verify version-specific or fast-changing facts (JDK features, tool versions) rather than rely on memory.

## 4. Environment
| Item | Value |
|---|---|
| OS | Windows + WSL2 (Ubuntu), kernel 6.18.54.1-microsoft-standard-WSL2 |
| Java | OpenJDK Temurin 21.0.13 (LTS), via SDKMAN |
| Build tool | Gradle 9.8.2, Groovy DSL (`build.gradle`), JUnit Jupiter |
| Project | `~/projects/zerocache` (Linux filesystem, NOT `/mnt/c`), subproject `app/`, package `com.venus.zerocache` |
| Repo | https://github.com/Abdechakour-Mc/zerocache (branch `main`), auth via `gh auth login` |
| Editor | VS Code with WSL extension + Extension Pack for Java |
| RDMA | Stock WSL kernel has `# CONFIG_INFINIBAND is not set`, so no `rdma_rxe` / SoftRoCE. `rdma-core`, `ibverbs-utils` and `perftest` are installed but unusable for now. |

**RDMA options (revisit when we reach the Transport interface):**
1. Custom WSL kernel with `CONFIG_INFINIBAND`, `CONFIG_INFINIBAND_USER_ACCESS`, `CONFIG_RDMA_RXE` (source: `microsoft/WSL2-Linux-Kernel`, then `kernel=` in `.wslconfig`). Verify exact options in the docs.
2. Small Linux VM or cloud instance whose kernel ships `rdma_rxe`.
3. Fall back to an RDMA transport built against a clean interface with a simulated implementation.

**Java note:** the foreign function API (for calling `libibverbs`) was preview in 21 and, as far as I recall, final in 22 (JEP 454). Verify before relying on it.

## 5. Course
Course: Advanced System Design 2025 (AlgoCamp). 60+ sections.
Link: https://courses.algocamp.io/learn/Advanced-System-Design-2025

**Foundational (1-9)**
01 Intro & system design basics, 02 [LLD] Intro & design principles basics, 03 [LLD] SOLID: SRP, OCP, LSP, ISP, 04 [LLD] Dependency Inversion, 05 [LLD] Simple rule engine (Rippling interview), 06 [LLD] Prototype pattern, 07 [LLD] Builder pattern, 08 [LLD] Approaching a machine coding problem, 09 [LLD] Iterator & Repository patterns

**Core frameworks, networking, architecture (10-29)**
10 Computer networks (DNS, torrents, TCP/UDP, IP, HTTP), 11 Spring Boot intro / env vars / build.gradle, 12 Understanding SOLID, 13 HTTP client homework, 14 MVC / repositories / services / controllers, 15 MVC with Spring Boot, 16 @RestController / @RequestMapping / @Service / @Repository, 17 Builder pattern (deep dive), 18 Databases with Spring Boot (JPA, ORM), 19 Spring Data JPA repositories, 20 MappedSuperclass / @ManyToOne / lazy fetching / N+1, 21 Many-to-many / JoinTable / soft delete / index, 22 Assignment: Cafeteria Menu API, 23 Flyway migrations / cascade types, 24 Assignment: Order and Review APIs, 25 Intro to AWS & cloud, 26 AWS EC2, 27 ResponseEntity & RestControllerAdvice, 28 Quiz 1, 29 Solving N+1 in Order APIs

(Sections 30+ not listed yet. Add them here as I see them.)

## 6. Concept-to-Zerocache map (plan, adapt as the course goes)
| Course topic | Zerocache piece |
|---|---|
| SRP, OCP, abstract classes, interfaces | `Cache` core, `EvictionPolicy` interface (FIFO, LIFO, later LRU/LFU/TTL), separate classes for storage / eviction |
| LSP, ISP | Every policy/cache substitutable (same tests run on all); small interfaces (e.g. `Evictable`, `Expirable`, `StatsProvider`) |
| DIP | Cache depends on abstractions; `Transport`, `Clock`, `Metrics` injected |
| Rule engine | Eviction / admission rules, configurable |
| Prototype, Builder | Cloning cache config; `CacheConfig` builder |
| Iterator, Repository | Iterating entries; `CacheRepository` / snapshot persistence |
| Networking (sec. 10) | TCP server with a simple text protocol, `Transport` interface; later RDMA transport |
| Spring Boot, MVC, REST (11-16, 27) | Admin / stats REST API, error handling with ControllerAdvice |
| JPA, Flyway (18-24) | Persist stats / config / snapshots |
| AWS EC2 (25-26) | Deploy the server |
| HLD topics (later) | Sharding, consistent hashing, replication |

## 7. Java and DSA refresher checklist
Tick as I actually use each in the project or in the daily exercise.
- [x] Classes, interfaces, `@Override`, `final`, constructors, `this.`
- [ ] Abstract classes (not used yet; interface-first so far)
- [x] Generics basics (`Cache<K, V>`, `EvictionPolicy<K>`, diamond `<>`)
- [x] `default` interface methods (`onKeyAccessed`)
- [ ] `ArrayList`, `LinkedList`, `LinkedHashMap`, `TreeMap`
- [x] `HashMap` (`containsKey`, `get`, `put`, `remove`)
- [ ] `HashSet`, `TreeSet`
- [x] `Deque` / `ArrayDeque` (`addFirst`, `addLast`, `getFirst`, `remove(Object)`); [ ] `Queue`, `PriorityQueue`
- [ ] Arrays, `Arrays.sort`, `Arrays.fill`, 2D arrays
- [ ] `String`, `StringBuilder`, `Character`
- [ ] Comparator and lambdas, streams
- [ ] `Optional`; [~] exceptions (`IllegalArgumentException`, `Objects.requireNonNull` used; custom/checked not yet)
- [ ] equals / hashCode contract
- [ ] Concurrency basics (`synchronized`, `ReentrantLock`, `ConcurrentHashMap`)
- [ ] Records, enums, switch expressions (Java 21)
- [ ] I/O and sockets (`java.net`, `java.nio`)
- [~] JUnit 5 (`@Test`, `assertEquals`, `assertNull` used; `assertThrows`, parameterized not yet)

## 8. Progress log

### Session 1 (Wed 2026-10-28), course: SRP, OCP, abstract classes vs interfaces
- Got the refresher: interface vs abstract class, Invoice SRP/OCP example (DiscountPolicy).
- Self-check questions (Shape interface, single vs multiple inheritance, why `final` on a template method) were not confirmed done.

### Session 2 (Thu 2026-10-29), project setup (Day 0)
- Picked Option 3 (distributed cache), name **Zerocache**.
- Environment verified. RDMA not possible on stock WSL kernel; deferred.
- Scaffolded with `gradle init` (Application, Java, single project, Groovy, JUnit Jupiter). `./gradlew run` and `test` work.
- Learned: `app/` is a Gradle subproject (source + tests), root holds build config; Groovy is Gradle's script language; JUnit is the test framework.
- Git: fixed missing identity, renamed branch `master` to `main`, pushed via `gh auth login` (browser login).

### Session 3 (Thu 2026-10-29), Day 1: swappable-eviction cache
**Built (all in `com.venus.zerocache`):**
- `Cache<K, V>`: `put`, `get` (null if missing), `remove`, `size`
- `EvictionPolicy<K>`: `onKeyAdded`, `onKeyRemoved`, `selectVictim`, `default onKeyAccessed`
- `InMemoryCache<K, V> implements Cache<K, V>`: `HashMap` store, capacity check, policy injected via constructor, validation (capacity <= 0, null key/policy)
- `FifoEvictionPolicy<K>` and `LifoEvictionPolicy<K>` (both `ArrayDeque`; only add/select side differs)
- `InMemoryCacheTest`: FIFO and LIFO eviction tests. `./gradlew test` green.

**Design notes (why):**
- Cache stores data; policy only tracks eviction order. Cache tells the policy what happened (events) and asks "who goes?". Keeps SRP.
- `onKeyAccessed` exists now (no-op default) so LRU later needs zero edits to `InMemoryCache` (OCP).
- LIFO was added with zero edits to `InMemoryCache`, so OCP is proven.
- `Cache` interface exists for DIP/OCP: callers depend on the abstraction, so sharded/remote/wrapper caches can be added later.
- Two structures (map + deque) is the cost of swappability; `LinkedHashMap` would merge them but hardcode the policy.

**Learned:** generics and type placeholders (`K`, `V`), diamond `<>` inference (declare types on the left, `<>` on the right), `this.` only needed when shadowing, `Deque` interface vs `ArrayDeque` class, `getFirst()` (throws on empty) vs `peekFirst()` (null), default methods, Arrange/Act/Assert test layout, F2 rename in VS Code (typo `Evication` was fixed).

## 9. Current status
- **Done:** Day 0 scaffold, Day 1 core design and implementation, FIFO + LIFO working, 2 tests green.

**Day 1 remaining (finish tomorrow before Day 2):**
1. Tests to add: overwrite existing key does not evict; `remove` frees space so next `put` doesn't evict; `get` of missing key returns null; capacity `0` throws `IllegalArgumentException` (`assertThrows`); null key throws `NullPointerException`.
2. Cleanup: remove stray `;` after method bodies in `LifoEvictionPolicy`, delete the auto-generated Javadoc that repeats the class name.
3. README.md (3 lines) and commit/push everything in small commits with clear messages (repo should have several commits).
4. LeetCode: #1 Two Sum (`HashMap`), #20 Valid Parentheses (`Deque` as stack), written without autocomplete doing the thinking.
5. Write one sentence each: why does this design satisfy SRP? why OCP? (send to Claude for feedback)
6. Optional stretch: run the same scenario against both policies with `@ParameterizedTest` (`@MethodSource`).

- **Next after that:** Day 2, tied to whatever the course covers next (likely LSP/ISP, then DIP). Tell Claude the topics from the next lecture.

## 10. Open questions / notes
- Should `get` return `Optional<V>` instead of `null`? Kept `null` (like `Map`); revisit in a later refactor.
- `onKeyRemoved` uses `Deque.remove(Object)`, which is O(n); revisit with LRU.
- `InMemoryCache` is not thread-safe yet (out of scope for now).

## 11. Repo and links
- GitHub repo: https://github.com/Abdechakour-Mc/zerocache
- Course: https://courses.algocamp.io/learn/Advanced-System-Design-2025
- Java cheatsheet (DSA syntax; note a few typos like `PiorityQueue`, `str.size()`): https://github.com/emanuele-em/java-cheatsheet