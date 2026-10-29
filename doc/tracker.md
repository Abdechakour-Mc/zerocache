# Zerocache: Learning Tracker

> **For Claude:** Read this file first. It replaces chat history. Follow the "Working agreement" below, continue from "Current status", and update the "Progress log" at the end of each session.

---

## 1. Goal
Refresh Java and build solid LLD/HLD skills through ONE small project that grows with the course. The final project goes on my CV and shows SOLID, clean code and system-design understanding. It should also cover the Java syntax needed for LeetCode (collections, strings, arrays, etc.).

## 2. Project: Zerocache
A pluggable-eviction, pluggable-transport cache server in Java.
- Starts as an in-memory cache with swappable eviction policies (LRU, LFU, TTL).
- Grows a network server (TCP first), then Spring Boot APIs, persistence, deployment, sharding/replication.
- Stretch goal: an RDMA transport behind the same `Transport` interface (I'm interested in RDMA and networking).
- Pitch for CV: "Pluggable-eviction, pluggable-transport cache server in Java with a TCP baseline and an RDMA transport."

## 3. Working agreement
- **Claude gives tasks, not code.** Each day: a task with acceptance criteria, the docs or topics to look up, and hints only if I ask.
- I write the code, push it to GitHub, and paste/link it. Claude reviews for SOLID violations, clean code and Java idioms.
- Small steps. Not so much that I get demotivated, not so little that I don't learn.
- One small LeetCode-style exercise per day that uses the same Java features I just touched.
- I report what the course covered each day, and the plan adapts to it.
- Claude should verify version-specific or fast-changing facts (JDK features, tool versions) rather than rely on memory.

## 4. Environment
| Item | Value |
|---|---|
| OS | Windows + WSL2 (Ubuntu), kernel 6.18.54.1-microsoft-standard-WSL2 |
| Java | OpenJDK Temurin 21.0.13 (LTS), via SDKMAN |
| Build tool | Gradle 9.8.2 |
| Project location | Linux filesystem (`~/projects/zerocache`), NOT `/mnt/c` (slow builds) |
| RDMA | Stock WSL kernel has `# CONFIG_INFINIBAND is not set`, so no `rdma_rxe` / SoftRoCE. `rdma-core`, `ibverbs-utils` and `perftest` are installed but unusable for now. |

**RDMA options (revisit when we reach the Transport interface):**
1. Custom WSL kernel with `CONFIG_INFINIBAND`, `CONFIG_INFINIBAND_USER_ACCESS`, `CONFIG_RDMA_RXE` (source: `microsoft/WSL2-Linux-Kernel`, then `kernel=` in `.wslconfig`). Verify exact options in the docs.
2. Small Linux VM or cloud instance whose kernel ships `rdma_rxe`.
3. Fall back to an RDMA transport built against a clean interface with a simulated implementation.

**Java note:** the foreign function API (for calling `libibverbs`) was preview in 21 and, as far as I recall, final in 22 (JEP 454). Verify before relying on it. May need to enable preview or upgrade the JDK via SDKMAN.

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
| SRP, OCP, abstract classes, interfaces | `Cache` core, `EvictionPolicy` interface (LRU, LFU, TTL), separate classes for storage / eviction / stats |
| LSP, ISP | Every policy substitutable; small interfaces (e.g. `Evictable`, `Expirable`, `StatsProvider`) |
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
- [ ] Classes, interfaces, abstract classes, `@Override`, `final`
- [ ] Generics (`Cache<K, V>`), bounded types
- [ ] `ArrayList`, `LinkedList`, `HashMap`, `LinkedHashMap`, `TreeMap`
- [ ] `HashSet`, `TreeSet`
- [ ] `Queue`, `Deque` / `ArrayDeque`, `PriorityQueue`
- [ ] Arrays, `Arrays.sort`, `Arrays.fill`, 2D arrays
- [ ] `String`, `StringBuilder`, `Character`
- [ ] Comparator and lambdas, streams
- [ ] `Optional`, exceptions (custom, checked vs unchecked)
- [ ] equals / hashCode contract
- [ ] Concurrency basics (`synchronized`, `ReentrantLock`, `ConcurrentHashMap`)
- [ ] Records, enums, switch expressions (Java 21)
- [ ] I/O and sockets (`java.net`, `java.nio`)
- [ ] JUnit 5 testing

## 8. Progress log

### Session 1 (Wed 2026-10-28), course: SRP, OCP, abstract classes vs interfaces
- Got the refresher: interface vs abstract class, and the Invoice SRP/OCP example (DiscountPolicy).
- Self-check questions given: Shape interface (add Triangle without editing others), single vs multiple inheritance, why `final` on a template method.
- Status: not yet confirmed done.

### Session 2 (Thu 2026-10-29), project setup
- Picked Option 3 (distributed cache) and the name **Zerocache**.
- Environment verified (see section 4). RDMA not possible on stock WSL kernel; deferred.
- **Day 0 task assigned:** create `~/projects/zerocache` with `gradle init` (decide application vs library, Java, single project, DSL, JUnit Jupiter), find where Main and tests live and run both, `git init` + `.gitignore` (build/, .gradle/), first commit, push to GitHub, three-line README.
- Status: in progress.

## 9. Current status
- **Done:** environment check, project chosen, tracker created.
- **In progress:** Day 0 (project scaffold).
- **Next:** Day 1 task tied to SRP/OCP (first small piece of the cache, with a pluggable policy via interface).

## 10. Open questions / notes
- (add anything I'm unsure about here)

## 11. Repo and links
- GitHub repo: (add URL after Day 0)
- Course: https://courses.algocamp.io/learn/Advanced-System-Design-2025
- Java cheatsheet (DSA syntax; note a few typos like `PiorityQueue`, `str.size()`): https://github.com/emanuele-em/java-cheatsheet