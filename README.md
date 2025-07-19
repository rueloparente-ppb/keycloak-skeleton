# 📚 Keycloak Skeleton - Microservices

This repository contains a sample microservices application built with Spring Boot. Its purpose is to demonstrate **Keycloak** as an identity provider within a microservices architecture featuring service discovery, a centralized API gateway, and role-based access control using Spring Security.

The project is configured as a **monorepo** and uses a `Taskfile` to simplify setup and management.

---

## 🏛️ Architecture Overview

The application follows a standard microservices pattern where all external requests are routed through an **API Gateway**. The gateway uses **Eureka** for service discovery to dynamically load-balance requests to the appropriate downstream service instances. It also enforces security policies, while **Keycloak** handles user authentication.

> **//TODO:** Add draw\.io diagram of architecture

---

## ✨ Features

* **Service Discovery**: Uses Spring Cloud Netflix Eureka for dynamic service registration and discovery.
* **Centralized Gateway**: Spring Cloud Gateway routes all traffic and acts as a single entry point.
* **Identity & Access Management**: Centralized authentication and role-based authorization using Keycloak.
* **OAuth 2.0 Security**: Services are configured as OAuth 2.0 Resource Servers to validate JWTs.
* **Simplified Tooling**: A `Taskfile` provides simple commands to start, stop, and clean the environment.

---

## 🛠️ Services

| Service             | Port | Description                                                           |
| ------------------- | ---- | --------------------------------------------------------------------- |
| `discovery-service` | 8761 | Eureka server for service registration and discovery.                 |
| `api-gateway`       | 8083 | The single entry point for all clients. Handles routing and security. |
| `hello-service`     | 8081 | A simple service with endpoints secured by user roles.                |
| `user-service`      | 8082 | Another example service with role-based security.                     |
| `keycloak`          | 8080 | The identity provider. The `issuer-uri` points here.                  |

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following tools installed on your system:

* Java 21+
* Maven 3.9+
* Docker & Docker Compose
* [Task](https://taskfile.dev) (a modern alternative to `make`)

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd <your-repository-directory>
```

### 2. Start the Infrastructure

Run the following command from the project root to start the Keycloak container:

```bash
task start_infra
```

### 3. Start the Application Services

Once the infrastructure is running, you must start the application services manually.

> ⚠️ **Important:** The services must be started in the following order:
>
> 1. `discovery-service`
> 2. `api-gateway`
> 3. `hello-service` & `user-service` (can be started in parallel)

#### Using an IDE:

Create a run configuration for each service and launch them in the correct sequence.

#### Using the Terminal:

Open a new terminal window for each command and run:

```bash
# Terminal 1: Start Discovery Service
./mvnw -pl discovery-service spring-boot:run

# Terminal 2: Start API Gateway
./mvnw -pl api-gateway spring-boot:run

# Terminal 3: Start Hello Service
./mvnw -pl hello-service spring-boot:run

# Terminal 4: Start User Service
./mvnw -pl user-service spring-boot:run
```

You can monitor the Eureka dashboard at:
👉 `http://localhost:8761`

---

## ⚙️ Usage

### Keycloak Credentials

A pre-configured realm (`spring-boot-microservices`) is imported on startup. You can log in to the Keycloak admin console at:

🔗 `http://localhost:8080`

**Admin Login:**

* **Username**: `admin`
* **Password**: `admin`

**Sample Users:**

| Username      | Password | Roles             |
| ------------- | -------- | ----------------- |
| `testuser`    | password | `USER`            |
| `testmanager` | password | `MANAGER` |

---

### Obtaining an Access Token

To access the secured endpoints, obtain a JWT from Keycloak.

#### Get a token for the user `testuser`

```bash
curl --location 'http://localhost:8080/realms/spring-boot-microservices/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=spring-microservices-postman-client' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=testuser' \
--data-urlencode 'password=password' \
--data-urlencode 'client_secret=PQCrieprWwnVpg120kihTlYWAncz5AiU'
```

#### Get a token for the manager `testmanager`

```bash
curl --location 'http://localhost:8080/realms/spring-boot-microservices/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=spring-microservices-postman-client' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=testmanager' \
--data-urlencode 'password=password' \
--data-urlencode 'client_secret=PQCrieprWwnVpg120kihTlYWAncz5AiU'
```

---

### Accessing Secured Endpoints

All requests go through the **API Gateway** on port `8083`.

#### Hello Service (`/api/hello`)

##### `/api/hello/user` (Requires `USER` role)

✅ With user token:

```bash
curl --location 'http://localhost:8083/api/hello/user' \
--header 'Authorization: Bearer $USER_TOKEN'
```

❌ Without token:

```bash
curl -i http://localhost:8083/api/hello/user
# Expected: HTTP/1.1 401 Unauthorized
```

##### `/api/hello/manager` (Requires `MANAGER` role)

✅ With manager token:

```bash
curl --location 'http://localhost:8083/api/hello/manager' \
--header 'Authorization: Bearer $MANAGER_TOKEN'
```

❌ With regular user token:

```bash
curl --location 'http://localhost:8083/api/hello/manager' \
--header 'Authorization: Bearer $USER_TOKEN'
# Expected: HTTP/1.1 403 Forbidden
```

---

## 🧹 Cleanup

To stop the services and remove Docker containers:

```bash
task clean
```

---

Let me know if you'd like this exported as a `.md` file or used in a specific platform like GitHub or Notion!
