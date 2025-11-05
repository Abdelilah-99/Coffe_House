# Understanding Spring Boot Embedded Tomcat, Sockets, and I/O

## 1. Overview

This document explains the internal workings of Spring Boot embedded Tomcat, focusing on:

- **Sockets** (listening and connected)
- **File descriptors (FDs)**
- **Thread management**
- **Blocking vs non-blocking I/O (BIO vs NIO)**
- **How Spring Boot configures and handles connections automatically**

It aims to clarify how connections are established, how data flows, and how Tomcat scales to thousands of clients.

---

## 2. Sockets

### 2.1 What is a Socket?

A **socket** is an endpoint for communication between two processes.

- In Unix-like systems, sockets are **file descriptors (FDs)** managed by the OS.
- Every socket has:
  - **Local endpoint**: IP + port on the server
  - **Remote endpoint**: IP + port on the client

#### Types:
- **Listening socket** – waits for new connections
- **Connected socket** – carries actual data for an established connection

---

### 2.2 Listening Socket

A listening socket is created by Tomcat at server startup on a specific port (e.g., 8080).

**Key characteristics:**
- Only **one listening socket per port**
- **Purpose**: Accept incoming TCP connection requests from clients
- **Never reads/writes HTTP data itself**
- Represented by a **single FD** in the OS

#### Lifecycle:

1. Tomcat starts embedded server
2. Calls `ServerSocketChannel.open()` and `bind(port)`
3. Configures socket as blocking or non-blocking
4. OS monitors it for incoming SYN requests
5. When a client connects, OS creates a **connected socket FD**

---

### 2.3 Connected Socket

A connected socket is created **per client** by the OS after a connection request.

**Key characteristics:**
- Carries actual **bidirectional data flow** between client and server
- Each connected socket has its **own FD**
- Number of connected sockets depends on:
  - `server.tomcat.max-connections` (maximum simultaneous connections)

#### Data handling:

- **Blocking I/O (BIO)**: Dedicated thread per connection blocks on `read()` / `write()`
- **Non-blocking I/O (NIO)**: Shared poller threads monitor all connected FDs for readiness; only ready sockets are processed

---

## 3. File Descriptors (FDs)

Both **listening** and **connected** sockets are FDs.

An FD acts as a handle in the kernel to:
- Track socket state
- Manage send/receive buffers
- Wake threads when data arrives

### Thread Usage:

| I/O Model | Thread Behavior |
|-----------|----------------|
| **BIO** | 1 FD → 1 thread (blocking) |
| **NIO** | 1 FD → shared poller thread + worker threads (event-driven) |

---

## 4. Blocking I/O (BIO) vs Non-blocking I/O (NIO)

| Feature | BIO | NIO |
|---------|-----|-----|
| **Thread per connection** | Yes | No (few poller threads) |
| **Socket behavior** | Thread blocks on `read()`/`write()` | Thread polls multiple FDs; OS signals readiness |
| **Scalability** | Limited (~thousands of threads max) | Very high (tens of thousands of FDs) |
| **CPU usage** | High (many blocked threads) | Efficient (threads sleep until OS signals readiness) |
| **Complexity** | Simple | More complex (event loop + selector) |

---

## 5. Spring Boot Tomcat Configuration

### 5.1 Key Configuration Properties

Spring Boot provides several properties to configure Tomcat's behavior:

```properties
# Maximum number of worker threads
server.tomcat.threads.max=200

# Minimum number of worker threads
server.tomcat.threads.min-spare=10

# Maximum number of connections the server will accept and process
server.tomcat.max-connections=10000

# Maximum queue length for incoming connection requests
server.tomcat.accept-count=100

# Connection timeout in milliseconds
server.tomcat.connection-timeout=20000
```

### 5.2 Default I/O Model

- Spring Boot uses **NIO** by default for Tomcat
- This provides better scalability for handling many concurrent connections
- Can be changed if needed, but NIO is recommended for most use cases

---

## 6. Connection Flow

### 6.1 Establishing a Connection

1. **Client initiates**: Sends TCP SYN packet to server:port
2. **OS receives**: Listening socket FD detects incoming connection
3. **Handshake**: TCP 3-way handshake completes (SYN, SYN-ACK, ACK)
4. **Connected socket created**: OS creates new FD for this connection
5. **Tomcat accepts**: Acceptor thread calls `accept()` and gets the new FD
6. **Processing**: Connection is registered with poller (NIO) or assigned to worker thread (BIO)

### 6.2 Data Exchange

#### NIO Model (Default):
1. Connected socket FD registered with **Selector**
2. **Poller thread** monitors multiple FDs using `select()` or `epoll()`
3. OS signals when FD is ready (data available to read)
4. Poller thread dispatches to **worker thread** from thread pool
5. Worker thread reads data, processes HTTP request, writes response
6. Connection kept alive or closed based on HTTP headers

#### BIO Model:
1. Dedicated thread assigned to connection
2. Thread blocks on `read()` waiting for data
3. Processes request when data arrives
4. Writes response and waits for next request or closes

---

## 7. Scaling Considerations

### 7.1 Maximum Connections

The `server.tomcat.max-connections` property determines:
- Maximum number of **connected socket FDs** that can exist simultaneously
- Once limit is reached, new connections wait in the accept queue

### 7.2 Thread Pool Size

The `server.tomcat.threads.max` property determines:
- Maximum number of **worker threads** for processing requests
- In NIO mode, this is much smaller than max-connections
- Example: 200 threads can handle 10,000 connections efficiently

### 7.3 Accept Queue

The `server.tomcat.accept-count` property determines:
- How many connections can wait when all threads are busy
- Connections beyond this limit are refused

---

## 8. Monitoring and Troubleshooting

### 8.1 Checking File Descriptors (Linux)

```bash
# Check FD limit for current process
ulimit -n

# List all FDs for a Java process
lsof -p <pid>

# Count socket FDs
lsof -p <pid> | grep -i socket | wc -l

# Check system-wide FD usage
cat /proc/sys/fs/file-nr
```

### 8.2 Common Issues

| Issue | Symptom | Solution |
|-------|---------|----------|
| **Too many FDs** | "Too many open files" error | Increase `ulimit -n` or reduce connections |
| **Thread exhaustion** | Slow response times | Increase `server.tomcat.threads.max` |
| **Connection timeout** | Clients can't connect | Increase `server.tomcat.max-connections` or `accept-count` |
| **Memory pressure** | OutOfMemoryError | Reduce max-connections or increase heap size |

---

## 9. Summary

- **Listening socket**: Single FD per port, accepts new connections
- **Connected socket**: One FD per client connection, carries actual data
- **BIO**: Simple but limited scalability (1 thread per connection)
- **NIO**: Complex but highly scalable (few threads, many connections)
- **Spring Boot default**: NIO with sensible defaults for most applications
- **Key tuning**: Balance `max-connections`, `threads.max`, and available system resources

---

## 10. References and Further Reading

- [Tomcat Configuration Documentation](https://tomcat.apache.org/tomcat-9.0-doc/config/)
- [Spring Boot Embedded Containers](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.webserver)
- [Java NIO Overview](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html)
- [Linux Socket Programming](https://man7.org/linux/man-pages/man7/socket.7.html)
