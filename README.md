# <img src="https://raw.githubusercontent.com/AdityaByte/Connect-Frontend/main/public/connect-logo.png" width="50"/> Connect - Backend

> This repository holds the Backend for Connect, a modern application designed to facilitate real-time chat and seamless communication.

[![License](https://img.shields.io/github/license/AdityaByte/Connect-Backend-SpringBoot)](https://github.com/AdityaByte/Connect-Backend-SpringBoot/blob/main/LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/AdityaByte/Connect-Backend-SpringBoot)](https://github.com/AdityaByte/Connect-Backend-SpringBoot/commits/main)
[![Build Status](https://img.shields.io/github/actions/workflow/status/AdityaByte/Connect-Backend-SpringBoot/ci.yml)](https://github.com/AdityaByte/Connect-Backend-SpringBoot/actions)
[![Stars](https://img.shields.io/github/stars/AdityaByte/Connect-Backend-SpringBoot?style=social)](https://github.com/AdityaByte/Connect-Backend-SpringBoot/stargazers)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-Secure-6DB33F?logo=springsecurity)](https://spring.io/projects/spring-security)
[![Kafka](https://img.shields.io/badge/Kafka-Event%20Streaming-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-DD0031?logo=redis&logoColor=white)](https://redis.io/)
[![MongoDB](https://img.shields.io/badge/MongoDB-NoSQL-47A248?logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Java](https://img.shields.io/badge/Java-17-007396?logo=java)](https://www.oracle.com/java/)
[![Lombok](https://img.shields.io/badge/Lombok-Annotation%20Magic-yellow?logo=lombok)](https://projectlombok.org/)
[![Functional Programming](https://img.shields.io/badge/Style-Functional-blue)](https://www.baeldung.com/java-functional-programming)



## Features

* 🔐 **Spring Security (MVC)** – Stateless JWT-based authentication and role-based authorization for both REST and WebSocket endpoints.
* 📲 **STOMP over WebSocket** – Real-time bi-directional communication using WebSocket and STOMP protocol.
* 🧾 **User Registration & Login APIs** – REST endpoints for secure signup, login, and token generation.
* 💬 **Chat APIs** – Send and receive chat messages through REST or live WebSocket connection.
* 🔄 **Kafka Messaging** – Asynchronous message streaming for decoupled and scalable communication pipelines.
* ⚡ **Redis Buffer** – Temporary in-memory storage for messages before being persisted to MongoDB.
* 🧠 **MongoDB Storage** – Flexible NoSQL data storage for users and message history.
* 🧼 **Clean MVC Architecture** – Organized into Controllers, Services, Repositories, DTOs, and Config layers.
* 🧪 **Functional Programming Style** – Uses `Optional`, lambdas, immutable DTOs, and method references for clean and modern Java code.
* 📦 **Configurable via `application.properties`** – Easily switch between dev, test, and prod profiles with Spring Profiles.

## Architecture

> Enforces a **Monolithic Architecture** with modular layering, built for scalability and clean separation of concerns.

<img src="https://raw.githubusercontent.com/AdityaByte/Connect-Frontend/main/public/connect-architecture.png" width="100%" alt="Connect Architecture Diagram" />

## Tech Stack

- ☕ **Java 17**
- 🌱 **Spring Boot 3**
- 🔐 **Spring Security (JWT)**
- 💬 **STOMP WebSocket**
- 📨 **Apache Kafka**
- ⚡ **Redis**
- 🍃 **MongoDB**
- 🧪 **Lombok + Functional Java**

## Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/AdityaByte/Connect-Backend-SpringBoot.git
cd Connect-Backend-SpringBoot

# 2. Start MongoDB, Redis, and Kafka locally or via Docker

# 3. Configure environment in:
src/main/resources/application.properties

# 4. Build and run the app
./mvnw spring-boot:run
```

## Author
<img src="https://github.com/AdityaByte.png" width="80" /> </p>
**Aditya Pawar**
🚀 Software Developer
🌐 [GitHub](https://github.com/AdityaByte) • 📬 [Email](mailto:cybergeek563@.com)