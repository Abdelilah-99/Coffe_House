# JPA and Hibernate: Complete Guide

## Table of Contents

1. [Overview](#1-overview)
2. [Core Concepts](#2-core-concepts)
3. [Entity Lifecycle](#3-entity-lifecycle)
4. [Relationships and Mappings](#4-relationships-and-mappings)
5. [Fetching Strategies](#5-fetching-strategies)
6. [Persistence Context](#6-persistence-context)
7. [Transaction Management](#7-transaction-management)
8. [Query Methods](#8-query-methods)
9. [Caching](#9-caching)
10. [Performance Optimization](#10-performance-optimization)
11. [Common Issues and Solutions](#11-common-issues-and-solutions)
12. [Best Practices](#12-best-practices)
13. [Configuration](#13-configuration)

---

## 1. Overview

### 1.1 What is JPA?

**JPA (Java Persistence API)** is a specification for object-relational mapping (ORM) in Java.

- **Not an implementation**, but a standard interface
- Defines how Java objects map to database tables
- Provides a consistent API for database operations
- Part of Jakarta EE (formerly Java EE)

### 1.2 What is Hibernate?

**Hibernate** is the most popular JPA implementation.

- **ORM framework** that implements JPA specification
- Provides additional features beyond JPA
- Handles database interactions automatically
- Manages SQL generation and execution

### 1.3 Architecture Overview

```
Application Layer
       ↓
JPA API (EntityManager, Repository)
       ↓
Hibernate Implementation
       ↓
JDBC Driver
       ↓
Database
```

---

## 2. Core Concepts

### 2.1 Entity

An **Entity** is a lightweight persistent domain object that represents a table in the database.

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors, getters, setters
}
```

**Key Annotations:**
- `@Entity`: Marks class as JPA entity
- `@Table`: Specifies table name (optional if class name matches table)
- `@Id`: Marks primary key field
- `@GeneratedValue`: Specifies how primary key is generated
- `@Column`: Customizes column mapping

### 2.2 EntityManager

The **EntityManager** is the primary JPA interface for interacting with the persistence context.

```java
@PersistenceContext
private EntityManager entityManager;

// Create
public void saveUser(User user) {
    entityManager.persist(user);
}

// Read
public User findUser(Long id) {
    return entityManager.find(User.class, id);
}

// Update
public User updateUser(User user) {
    return entityManager.merge(user);
}

// Delete
public void deleteUser(Long id) {
    User user = entityManager.find(User.class, id);
    if (user != null) {
        entityManager.remove(user);
    }
}
```

### 2.3 Primary Key Generation Strategies

| Strategy | Description | Use Case |
|----------|-------------|----------|
| `GenerationType.IDENTITY` | Database auto-increment | MySQL, PostgreSQL with SERIAL |
| `GenerationType.SEQUENCE` | Database sequence | Oracle, PostgreSQL with SEQUENCE |
| `GenerationType.TABLE` | Separate table for IDs | Portable across databases |
| `GenerationType.AUTO` | Provider chooses strategy | Default, database-dependent |

```java
// Identity strategy
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Sequence strategy
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
private Long id;

// UUID strategy
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

---

## 3. Entity Lifecycle

### 3.1 Entity States

```
    ┌──────────┐
    │   NEW    │ (Transient)
    └────┬─────┘
         │ persist()
         ↓
    ┌──────────┐
    │ MANAGED  │ (Persistent)
    └────┬─────┘
         │ detach() / clear()
         ↓
    ┌──────────┐
    │ DETACHED │
    └────┬─────┘
         │ merge()
         ↓
    ┌──────────┐
    │ MANAGED  │
    └────┬─────┘
         │ remove()
         ↓
    ┌──────────┐
    │ REMOVED  │
    └──────────┘
```

### 3.2 State Descriptions

| State | Description | Tracked by EntityManager? | In Database? |
|-------|-------------|---------------------------|--------------|
| **Transient (New)** | Object created with `new`, not associated with persistence context | No | No |
| **Managed (Persistent)** | Object associated with persistence context, changes tracked | Yes | Yes |
| **Detached** | Was managed but session closed, changes not tracked | No | Yes |
| **Removed** | Marked for deletion, will be removed on transaction commit | Yes | Pending deletion |

### 3.3 Lifecycle Example

```java
// Transient state
User user = new User("john", "john@example.com");

// Managed state (after persist)
entityManager.persist(user);

// Still managed (updates tracked automatically)
user.setEmail("newemail@example.com"); // Will UPDATE in DB on flush

// Detached state
entityManager.detach(user);
user.setEmail("another@example.com"); // NOT tracked

// Managed again (after merge)
User managedUser = entityManager.merge(user); // Now changes tracked

// Removed state
entityManager.remove(managedUser); // Will DELETE on flush
```

---

## 4. Relationships and Mappings

### 4.1 One-to-One

One entity instance is associated with one instance of another entity.

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;
}

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bio;
    private String avatar;
}
```

**Best Practice:**
- Use `@OneToOne` on the side with the foreign key
- Use `mappedBy` on the inverse side
- Consider `fetch = FetchType.LAZY` to avoid unnecessary queries

### 4.2 One-to-Many / Many-to-One

One entity instance is associated with multiple instances of another entity.

```java
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // Helper methods
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
```

**Best Practices:**
- Always use `@ManyToOne` with `fetch = FetchType.LAZY`
- Use `mappedBy` on the `@OneToMany` side
- Provide helper methods to maintain both sides of the relationship
- Consider `orphanRemoval = true` for true parent-child relationships

### 4.3 Many-to-Many

Multiple instances of one entity are associated with multiple instances of another entity.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    // Helper methods
    public void enrollCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    public void unenrollCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this);
    }
}

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    // Helper methods
    public void enrollCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    public void unenrollCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this);
    }
}

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

**Best Practices:**
- Use `Set` instead of `List` for better performance
- Define `@JoinTable` on one side (owner)
- Use `mappedBy` on the inverse side
- Always maintain both sides of the relationship

### 4.4 Many-to-Many with Extra Columns

When the join table needs additional attributes, use two `@ManyToOne` relationships.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
}

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments = new ArrayList<>();
}

@Entity
@Table(name = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime enrolledAt;
    private Integer grade;

    // Constructors, getters, setters
}
```

### 4.5 Cascade Types

| Cascade Type | Description | Use Case |
|--------------|-------------|----------|
| `CascadeType.PERSIST` | Persist operation cascades to related entities | Save parent with children |
| `CascadeType.MERGE` | Merge operation cascades | Update parent with children |
| `CascadeType.REMOVE` | Remove operation cascades | Delete parent deletes children |
| `CascadeType.REFRESH` | Refresh operation cascades | Reload parent reloads children |
| `CascadeType.DETACH` | Detach operation cascades | Detach parent detaches children |
| `CascadeType.ALL` | All operations cascade | Parent fully controls children |

```java
@OneToMany(
    mappedBy = "post",
    cascade = CascadeType.ALL,  // All operations cascade
    orphanRemoval = true         // Delete children when removed from collection
)
private List<Comment> comments = new ArrayList<>();
```

---

## 5. Fetching Strategies

### 5.1 Eager vs Lazy Loading

| Strategy | Behavior | Pros | Cons |
|----------|----------|------|------|
| **EAGER** | Loads related entities immediately | Always available, no proxy issues | Can cause N+1 problem, loads unnecessary data |
| **LAZY** | Loads related entities on first access | Better performance, loads on demand | Can cause LazyInitializationException |

### 5.2 Default Fetch Types

| Relationship | Default Fetch Type |
|--------------|-------------------|
| `@OneToOne` | `FetchType.EAGER` |
| `@ManyToOne` | `FetchType.EAGER` |
| `@OneToMany` | `FetchType.LAZY` |
| `@ManyToMany` | `FetchType.LAZY` |

**Recommendation:** Always explicitly set `fetch = FetchType.LAZY` for `@ManyToOne` and `@OneToOne`.

### 5.3 Solving the N+1 Problem

The **N+1 problem** occurs when fetching a collection of N entities triggers 1 query for the collection + N queries for related entities.

#### Problem Example:
```java
// This will execute 1 + N queries (BAD)
List<Post> posts = postRepository.findAll();
for (Post post : posts) {
    System.out.println(post.getAuthor().getName()); // Triggers query per post
}
```

#### Solution 1: JOIN FETCH
```java
@Query("SELECT p FROM Post p JOIN FETCH p.author")
List<Post> findAllWithAuthor();
```

#### Solution 2: @EntityGraph
```java
@EntityGraph(attributePaths = {"author", "comments"})
@Query("SELECT p FROM Post p")
List<Post> findAllWithAuthorAndComments();
```

#### Solution 3: Batch Fetching
```java
@Entity
public class Post {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @BatchSize(size = 10) // Fetch in batches of 10
    private User author;
}
```

---

## 6. Persistence Context

### 6.1 What is Persistence Context?

The **Persistence Context** is a cache of managed entity instances.

- Acts as a **first-level cache** (enabled by default)
- Tracks entity state changes automatically
- Ensures entity uniqueness within the context
- Flushes changes to database at specific points

### 6.2 Persistence Context Scope

| Scope | Description | Typical Use |
|-------|-------------|-------------|
| **Transaction-scoped** | Lives for the duration of a transaction | Default in Spring Boot |
| **Extended** | Lives beyond transaction boundaries | Stateful session beans |

### 6.3 Flush Modes

**Flush** is the process of synchronizing persistence context state with the database.

```java
// Manual flush
entityManager.flush();

// Flush modes
entityManager.setFlushMode(FlushModeType.AUTO);   // Default, flushes before query
entityManager.setFlushMode(FlushModeType.COMMIT); // Flushes only on commit
```

**When Flush Occurs:**
1. Before executing a JPQL/HQL query
2. Before transaction commit
3. Manual call to `flush()`

### 6.4 First-Level Cache (Persistence Context)

```java
@Transactional
public void demonstrateFirstLevelCache() {
    // First access - queries database
    User user1 = entityManager.find(User.class, 1L);

    // Second access - returns from cache (no query)
    User user2 = entityManager.find(User.class, 1L);

    // Both references point to same instance
    assert user1 == user2; // true
}
```

**Cache Operations:**
```java
// Check if entity is managed
boolean isManaged = entityManager.contains(user);

// Clear entire cache
entityManager.clear();

// Detach specific entity
entityManager.detach(user);

// Refresh entity from database
entityManager.refresh(user); // Overwrites changes
```

---

## 7. Transaction Management

### 7.1 ACID Properties

| Property | Description |
|----------|-------------|
| **Atomicity** | All operations succeed or all fail |
| **Consistency** | Database remains in valid state |
| **Isolation** | Concurrent transactions don't interfere |
| **Durability** | Committed changes are permanent |

### 7.2 Spring @Transactional

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void transferCredits(Long fromId, Long toId, int amount) {
        User from = userRepository.findById(fromId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        User to = userRepository.findById(toId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        from.setCredits(from.getCredits() - amount);
        to.setCredits(to.getCredits() + amount);

        // Both updates committed together or rolled back on exception
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
```

### 7.3 Transaction Attributes

```java
@Transactional(
    propagation = Propagation.REQUIRED,      // Default
    isolation = Isolation.DEFAULT,           // Database default
    readOnly = false,                        // Allow writes
    timeout = 30,                            // 30 seconds
    rollbackFor = Exception.class,           // Rollback on any exception
    noRollbackFor = IllegalArgumentException.class
)
public void complexOperation() {
    // ...
}
```

### 7.4 Propagation Levels

| Propagation | Description |
|-------------|-------------|
| `REQUIRED` | Use existing transaction or create new (default) |
| `REQUIRES_NEW` | Always create new transaction, suspend existing |
| `MANDATORY` | Must have existing transaction, else throw exception |
| `SUPPORTS` | Use transaction if exists, else execute non-transactionally |
| `NOT_SUPPORTED` | Execute non-transactionally, suspend existing transaction |
| `NEVER` | Execute non-transactionally, throw exception if transaction exists |
| `NESTED` | Execute within nested transaction if supported |

### 7.5 Isolation Levels

| Level | Description | Dirty Read | Non-Repeatable Read | Phantom Read |
|-------|-------------|------------|---------------------|--------------|
| `READ_UNCOMMITTED` | Lowest isolation | Yes | Yes | Yes |
| `READ_COMMITTED` | Prevents dirty reads | No | Yes | Yes |
| `REPEATABLE_READ` | Prevents dirty & non-repeatable reads | No | No | Yes |
| `SERIALIZABLE` | Highest isolation | No | No | No |

---

## 8. Query Methods

### 8.1 Spring Data JPA Query Methods

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // Derived query methods
    List<User> findByUsername(String username);
    List<User> findByEmailContaining(String email);
    List<User> findByAgeGreaterThan(int age);
    List<User> findByUsernameAndEmail(String username, String email);
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Sorting
    List<User> findByAgeGreaterThanOrderByUsernameAsc(int age);

    // Pagination
    Page<User> findByAgeGreaterThan(int age, Pageable pageable);

    // Limiting results
    List<User> findTop10ByOrderByCreatedAtDesc();
    User findFirstByOrderByUsernameAsc();

    // Exists queries
    boolean existsByEmail(String email);

    // Count queries
    long countByAgeGreaterThan(int age);

    // Delete queries
    void deleteByUsername(String username);
    List<User> removeByAgeGreaterThan(int age);
}
```

### 8.2 @Query Annotation

#### JPQL Queries
```java
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword%")
    List<Post> searchByTitle(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    @Query("SELECT p FROM Post p JOIN FETCH p.comments WHERE p.author.id = :authorId")
    List<Post> findByAuthorIdWithComments(@Param("authorId") Long authorId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId")
    long countByAuthorId(@Param("authorId") Long authorId);
}
```

#### Native SQL Queries
```java
@Query(value = "SELECT * FROM posts WHERE created_at > :date", nativeQuery = true)
List<Post> findRecentPosts(@Param("date") LocalDateTime date);

@Query(value = "SELECT p.*, u.username FROM posts p JOIN users u ON p.author_id = u.id WHERE p.published = true", nativeQuery = true)
List<Post> findPublishedPostsWithAuthor();
```

### 8.3 @Modifying Queries

```java
@Modifying
@Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
int incrementViewCount(@Param("id") Long id);

@Modifying
@Query("DELETE FROM Comment c WHERE c.post.id = :postId")
void deleteAllCommentsByPostId(@Param("postId") Long postId);

// Bulk update
@Modifying
@Transactional
@Query("UPDATE User u SET u.active = false WHERE u.lastLogin < :date")
int deactivateInactiveUsers(@Param("date") LocalDateTime date);
```

**Important:** Always use `@Modifying` with `@Transactional` for updates/deletes.

### 8.4 Projections

#### Interface-based Projections
```java
public interface UserSummary {
    String getUsername();
    String getEmail();
    LocalDateTime getCreatedAt();
}

public interface UserRepository extends JpaRepository<User, Long> {
    List<UserSummary> findByAgeGreaterThan(int age);
}
```

#### Class-based Projections (DTOs)
```java
public class UserDTO {
    private String username;
    private String email;

    public UserDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters
}

@Query("SELECT new com.example.dto.UserDTO(u.username, u.email) FROM User u WHERE u.age > :age")
List<UserDTO> findUserDTOsByAge(@Param("age") int age);
```

---

## 9. Caching

### 9.1 Cache Levels

```
┌─────────────────────────────────────┐
│   First-Level Cache                 │
│   (Persistence Context)             │
│   - Always enabled                  │
│   - Transaction-scoped              │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│   Second-Level Cache                │
│   (SessionFactory-level)            │
│   - Optional                        │
│   - Application-wide                │
│   - Requires cache provider         │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
│   Query Cache                       │
│   - Caches query results            │
│   - Works with second-level cache   │
└─────────────────────────────────────┘
```

### 9.2 Second-Level Cache Configuration

#### Add Dependency (Ehcache example)
```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
</dependency>
```

#### Application Properties
```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
spring.jpa.properties.hibernate.javax.cache.provider=org.ehcache.jsr107.EhcacheCachingProvider
spring.jpa.properties.hibernate.cache.use_query_cache=true
```

#### Enable Caching on Entity
```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
}
```

### 9.3 Cache Strategies

| Strategy | Description | Use Case |
|----------|-------------|----------|
| `READ_ONLY` | Best performance, immutable data | Static reference data |
| `READ_WRITE` | Standard strategy with locking | Most use cases |
| `NONSTRICT_READ_WRITE` | No locking, may have stale data | Data that changes infrequently |
| `TRANSACTIONAL` | Full transactional support | Distributed caches |

### 9.4 Query Cache

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findByCategory(@Param("category") String category);
}
```

---

## 10. Performance Optimization

### 10.1 N+1 Problem Solutions Summary

```java
// Problem: N+1 queries
List<Post> posts = postRepository.findAll();
posts.forEach(post -> System.out.println(post.getAuthor().getName()));

// Solution 1: JOIN FETCH
@Query("SELECT p FROM Post p JOIN FETCH p.author")
List<Post> findAllWithAuthor();

// Solution 2: @EntityGraph
@EntityGraph(attributePaths = "author")
List<Post> findAll();

// Solution 3: Batch Size
@BatchSize(size = 25)
@ManyToOne
private User author;
```

### 10.2 Pagination

```java
@Service
public class PostService {

    public Page<Post> getPosts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortBy).descending()
        );

        return postRepository.findAll(pageable);
    }
}
```

### 10.3 Batch Operations

```java
@Repository
public class BatchUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void batchInsert(List<User> users) {
        int batchSize = 50;

        for (int i = 0; i < users.size(); i++) {
            entityManager.persist(users.get(i));

            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}
```

#### Configuration
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.batch_versioned_data=true
```

### 10.4 Use DTOs for Read Operations

```java
// Instead of loading full entity
@Query("SELECT new com.example.dto.PostSummaryDTO(p.id, p.title, p.createdAt) FROM Post p")
List<PostSummaryDTO> findAllSummaries();

// This loads only required columns
public record PostSummaryDTO(Long id, String title, LocalDateTime createdAt) {}
```

### 10.5 Avoid Cartesian Products

```java
// BAD: Multiple JOIN FETCH can cause cartesian product
@Query("SELECT p FROM Post p JOIN FETCH p.comments JOIN FETCH p.tags")
List<Post> findAllWithCommentsAndTags();

// GOOD: Use multiple queries or @EntityGraph with careful design
@EntityGraph(attributePaths = {"comments", "tags"})
@Query("SELECT DISTINCT p FROM Post p")
List<Post> findAllWithCommentsAndTags();
```

---

## 11. Common Issues and Solutions

### 11.1 LazyInitializationException

**Problem:**
```java
@Transactional
public Post getPost(Long id) {
    return postRepository.findById(id).orElseThrow();
}

// Outside transaction
Post post = postService.getPost(1L);
post.getComments().size(); // LazyInitializationException!
```

**Solutions:**

#### Solution 1: Fetch eagerly
```java
@Query("SELECT p FROM Post p JOIN FETCH p.comments WHERE p.id = :id")
Optional<Post> findByIdWithComments(@Param("id") Long id);
```

#### Solution 2: Use @Transactional on calling method
```java
@Transactional(readOnly = true)
public void displayPost(Long id) {
    Post post = postService.getPost(id);
    post.getComments().forEach(System.out::println); // Works
}
```

#### Solution 3: Use DTO
```java
public PostWithCommentsDTO getPostWithComments(Long id) {
    Post post = postRepository.findByIdWithComments(id).orElseThrow();
    return new PostWithCommentsDTO(post); // Convert in transaction
}
```

### 11.2 MultipleBagFetchException

**Problem:**
```java
@Entity
public class Post {
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Tag> tags = new ArrayList<>();
}

// This throws MultipleBagFetchException
@Query("SELECT p FROM Post p JOIN FETCH p.comments JOIN FETCH p.tags")
List<Post> findAllWithCommentsAndTags();
```

**Solutions:**

#### Solution 1: Use Set instead of List
```java
@OneToMany(mappedBy = "post")
private Set<Comment> comments = new HashSet<>();

@OneToMany(mappedBy = "post")
private Set<Tag> tags = new HashSet<>();
```

#### Solution 2: Use multiple queries
```java
@EntityGraph(attributePaths = "comments")
List<Post> findAllWithComments();

@EntityGraph(attributePaths = "tags")
Map<Long, Post> findByIdInWithTags(List<Long> ids);
```

#### Solution 3: Use DISTINCT with Set
```java
@Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.comments JOIN FETCH p.tags")
List<Post> findAllWithCommentsAndTags();
```

### 11.3 Detached Entity Passed to Persist

**Problem:**
```java
User user = new User();
user.setId(1L); // Setting ID makes it detached
entityManager.persist(user); // Exception!
```

**Solution:**
```java
// Use merge for detached entities
User user = new User();
user.setId(1L);
entityManager.merge(user); // Correct

// Or let Hibernate generate ID
User user = new User();
entityManager.persist(user); // Correct
```

### 11.4 Failed to Lazily Initialize

**Problem:** Accessing lazy collection outside transaction.

**Solution:** Use `@Transactional` or fetch eagerly.

---

## 12. Best Practices

### 12.1 Entity Design

1. **Always override equals() and hashCode()**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

2. **Use business keys when possible**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User)) return false;
    User user = (User) o;
    return Objects.equals(email, user.email); // Use natural key
}
```

3. **Avoid Lombok @Data with JPA entities**
```java
// BAD
@Data
@Entity
public class User { }

// GOOD
@Getter
@Setter
@Entity
public class User {
    // Manually implement equals/hashCode/toString
}
```

### 12.2 Relationship Best Practices

1. **Always use bidirectional helper methods**
```java
public void addComment(Comment comment) {
    comments.add(comment);
    comment.setPost(this);
}
```

2. **Use Set for many-to-many relationships**
```java
@ManyToMany
private Set<Tag> tags = new HashSet<>(); // Not List
```

3. **Be explicit with fetch types**
```java
@ManyToOne(fetch = FetchType.LAZY) // Always explicit
private User author;
```

4. **Avoid bidirectional @OneToOne**
```java
// Prefer unidirectional from child to parent
@OneToOne
@JoinColumn(name = "user_id")
private User user;
```

### 12.3 Query Best Practices

1. **Use @Transactional(readOnly = true) for read operations**
```java
@Transactional(readOnly = true)
public List<User> getAllUsers() {
    return userRepository.findAll();
}
```

2. **Use projections for DTOs**
```java
@Query("SELECT new com.example.dto.UserDTO(u.id, u.name) FROM User u")
List<UserDTO> findAllUserDTOs();
```

3. **Always use pagination for large datasets**
```java
Page<Post> findAll(Pageable pageable);
```

4. **Use native queries sparingly**
```java
// Prefer JPQL over native SQL
@Query("SELECT u FROM User u WHERE u.active = true") // JPQL
@Query(value = "SELECT * FROM users WHERE active = 1", nativeQuery = true) // Native
```

### 12.4 Performance Best Practices

1. **Enable query logging in development**
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

2. **Use connection pooling**
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

3. **Enable batch processing**
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

4. **Use appropriate column types**
```java
@Column(columnDefinition = "TEXT") // For large strings
private String content;

@Column(precision = 10, scale = 2) // For money
private BigDecimal price;
```

---

## 13. Configuration

### 13.1 Application Properties

```properties
# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Connection pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Performance
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.query.in_clause_parameter_padding=true
spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch=true

# Caching
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
spring.jpa.properties.hibernate.cache.use_query_cache=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.org.hibernate.stat=DEBUG
spring.jpa.properties.hibernate.generate_statistics=true
```

### 13.2 DDL Auto Modes

| Mode | Description | Use Case |
|------|-------------|----------|
| `none` | No action | Production (default) |
| `validate` | Validate schema, don't make changes | Production with Flyway/Liquibase |
| `update` | Update schema | Development (risky) |
| `create` | Drop and create schema | Testing |
| `create-drop` | Create on start, drop on shutdown | Integration tests |

**Recommendation:** Use `validate` in production with Flyway or Liquibase for migrations.

### 13.3 Logging Configuration

```properties
# SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Show parameters
spring.jpa.properties.hibernate.use_sql_comments=true

# Performance statistics
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=DEBUG

# Cache logging
logging.level.org.hibernate.cache=DEBUG
```

---

## 14. Resources and References

### Official Documentation
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
- [JPA Specification](https://jakarta.ee/specifications/persistence/)

### Recommended Reading
- "Java Persistence with Hibernate" by Christian Bauer
- "High-Performance Java Persistence" by Vlad Mihalcea
- [Vlad Mihalcea's Blog](https://vladmihalcea.com/)
- [Baeldung JPA Tutorials](https://www.baeldung.com/learn-jpa-hibernate)

### Tools
- **JPA Buddy** - IntelliJ IDEA plugin for JPA development
- **Hibernate Validator** - Bean validation implementation
- **Flyway** - Database migration tool
- **Liquibase** - Database version control

---

## Appendix: Quick Reference

### Common Annotations

| Annotation | Purpose |
|------------|---------|
| `@Entity` | Mark class as JPA entity |
| `@Table` | Specify table name |
| `@Id` | Mark primary key |
| `@GeneratedValue` | Auto-generate primary key |
| `@Column` | Customize column mapping |
| `@ManyToOne` | Many-to-one relationship |
| `@OneToMany` | One-to-many relationship |
| `@OneToOne` | One-to-one relationship |
| `@ManyToMany` | Many-to-many relationship |
| `@JoinColumn` | Specify foreign key column |
| `@JoinTable` | Specify join table |
| `@Transactional` | Mark transactional method |
| `@Query` | Define custom query |
| `@Modifying` | Mark modifying query |
| `@EntityGraph` | Define fetch graph |

### EntityManager Methods

| Method | Purpose |
|--------|---------|
| `persist(entity)` | Make transient entity persistent |
| `merge(entity)` | Merge detached entity |
| `remove(entity)` | Remove entity |
| `find(Class, id)` | Find by primary key |
| `flush()` | Synchronize with database |
| `clear()` | Clear persistence context |
| `detach(entity)` | Detach entity from context |
| `refresh(entity)` | Reload from database |
| `createQuery(jpql)` | Create JPQL query |

---

**Last Updated:** 2025-11-05
