# Order Management Microservices

A Spring Boot–based e-commerce order management backend built with a microservices architecture. It simulates a real-world e-commerce system where users authenticate, products are created and viewed, inventory is maintained and reserved, and customers create or cancel orders — using independent services, separate databases, JWT authentication, an API Gateway, synchronous REST communication, and asynchronous Kafka events.

## Overview

This project goes beyond basic CRUD by demonstrating:

- Independent microservices, each with its own database
- JWT-based authentication and role-based authorization
- An API Gateway as the single client-facing entry point
- Synchronous service-to-service communication (RestTemplate and OpenFeign)
- Asynchronous, event-driven communication via Apache Kafka
- Inventory reservation/release workflows tied to order lifecycle
- JPA/Hibernate entity relationships, lazy loading, and fetch joins
- Early-stage distributed data consistency handling across services

The four core services are the **User Service**, **Product Service**, **Inventory Service**, and **Order Service**, fronted by an **API Gateway** and backed by a shared JWT security module.

## Architecture

Each microservice owns its own PostgreSQL database — there is no shared/central database. A service that needs data owned by another service must go through that service's API rather than querying its tables directly.

| Service | Responsibility | Port |
|---|---|---|
| User Service | Registration, login, JWT issuance | 8081 |
| Product Service | Product creation and retrieval | 8082 |
| Inventory Service | Stock tracking, reservation, release | 8083 |
| Order Service | Order creation, cancellation, orchestration | 8084 |
| API Gateway | Routing and centralized security | — |

**Communication patterns:**
- **Order → Inventory**: synchronous REST (RestTemplate / OpenFeign), because order creation needs an immediate reservation result.
- **Product → Inventory**: asynchronous, via Kafka events, because inventory initialization does not need to block product creation.

## Services

### User Service
Handles registration and login. On successful login, issues a JWT containing the user's `userId` (subject) and role. The token intentionally carries only what's needed for auth/authorization — not full profile data (email, name, etc.). The JWT secret is externalized via configuration rather than hard-coded.

- `POST /login` — authenticate and receive a JWT

### Product Service
Owns product data and exposes CRUD-style read/create endpoints. Product creation can publish a **product-created** event to Kafka, which the Inventory Service consumes to initialize the corresponding inventory record — so Product Service never touches the Inventory database directly. Product creation is intended to be restricted to a seller-type role; retrieval can remain public, depending on configured security rules.

- `POST /products` — create a product
- `GET /products` — retrieve all products
- `GET /products/{id}` — retrieve a specific product

### Inventory Service
The sole owner of stock data. Handles initialization/updates, reservation (on order creation), and release (on order cancellation).

- `POST /inventory/updateStock` — create or update stock for a product
- `POST /inventory/reserve` — reserve stock for requested product/quantity pairs
- `POST /inventory/release` — release previously reserved stock
- `GET /inventory/{productId}` — retrieve inventory for a product

The Order Service never manipulates inventory tables directly; it always asks the Inventory Service to perform the operation.

### Order Service
Contains the core business workflow. Models `Order` and `OrderItem` as JPA entities with a one-to-many relationship (`@OneToMany`, `@ManyToOne`, `mappedBy`, `@JoinColumn`, cascading), and uses a fetch-join query to retrieve an order together with its items in a single query where needed.

- `POST /order/create` — create an order and attempt inventory reservation
- `POST /order/cancel` — cancel an order and release reserved inventory
- `GET /order/{id}` — retrieve an order

## Order Lifecycle

Order creation and cancellation involve coordination across two services (Order and Inventory) and two databases, so the workflow is modeled explicitly rather than handled as a single database transaction:

1. Order is created in memory / initiated as `CREATED`.
2. Order Service requests inventory reservation for each product in the order.
3. If **all** reservations succeed → order is persisted as `CREATED`.
4. If **any** reservation fails → the order is **not** persisted; the response identifies which product(s) couldn't be reserved (`STOCK_UNAVAILABLE` case).
5. On cancellation (`POST /order/cancel`), the Order Service requests inventory release for the order's items, then moves the order to `CANCELLED`.

Implemented order statuses: `CREATED`, `CANCELLED` 


## Kafka Event Flow

Product Service acts as a Kafka **producer**; Inventory Service acts as a **consumer**.

1. A product is created via `POST /products`.
2. Product Service completes the product write.
3. A product-created event is published to Kafka.
4. Kafka stores/delivers the event.
5. Inventory Service consumes the event.
6. Inventory Service creates or updates the corresponding inventory record.

This keeps Product Service and Inventory Service loosely coupled — no synchronous call is required for this flow.

## Security

Authentication and authorization are handled via JWT, with a shared security module providing reusable JWT logic across services.

**Authentication flow:**
1. User submits credentials to the User Service.
2. User Service validates credentials and issues a JWT.
3. Client attaches the JWT to subsequent requests.
4. Services validate the token and extract identity/role.
5. Role information drives authorization decisions.

**Authentication vs. Authorization:**
- *Authentication* — "Who is making this request?"
- *Authorization* — "Is this user allowed to perform this operation?"

The JWT secret is supplied through external configuration (`jwt.secret=${jwt.secret}`) rather than committed to source control.

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL (one per service) |
| API Gateway | Spring Cloud Gateway |
| Sync communication | Spring Cloud OpenFeign, RestTemplate |
| Async communication | Apache Kafka |
| Security | JWT |
| Infrastructure | Docker |
| API Documentation | Swagger / OpenAPI |
| Version Control | Git |

## API Reference

### User Service — `:8081`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/login` | Authenticate and return a JWT |

### Product Service — `:8082`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/products` | Create a product |
| GET | `/products` | Retrieve all products |
| GET | `/products/{id}` | Retrieve a specific product |

### Inventory Service — `:8083`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/inventory/reserve` | Reserve stock |
| POST | `/inventory/release` | Release previously reserved stock |
| GET | `/inventory/{productId}` | Get inventory for a product |
| POST | `/inventory/updateStock` | Create/update stock information |

### Order Service — `:8084`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/order/create` | Create an order and attempt reservation |
| POST | `/order/cancel` | Cancel an order and release inventory |
| GET | `/order/{id}` | Retrieve an order |

## What This Project Demonstrates

- Spring Boot microservices and service decomposition
- Database-per-service architecture
- REST API design
- Synchronous inter-service communication (RestTemplate, OpenFeign)
- Kafka producers/consumers and event-driven architecture
- JWT authentication and role-based authorization
- API Gateway routing
- PostgreSQL, JPA, and Hibernate, including entity relationships, lazy loading, and fetch joins
- Order state management and inventory reservation/release
- Early distributed-data-consistency considerations across services
- Docker-based infrastructure and Swagger/OpenAPI documentation

## Planned Extensions

These are architectural directions under consideration and **are not implemented yet**. They're listed here for roadmap clarity, not as existing features:

- **Redis** — idempotency for operations like inventory reserve/release, to safely handle duplicate requests or events.
- **Notification Service** — a new service consuming order-created events to send customer email notifications.
- **Payment Service** — an independently persisted service to model a realistic payment step in the order workflow.
- **Saga / Compensation** — compensating transactions across Order, Inventory, and Payment when one step in the workflow fails (e.g., inventory reserved but payment fails).
- **Transactional Outbox** — addressing the dual-write problem between a database update and a Kafka event publish.
- **Kafka retries & dead-letter handling** — more production-grade event processing with retry policies and dead-letter topics.

## Project Goals

This project exists to demonstrate the transition from a simple Spring Boot CRUD app to a distributed backend system, exploring questions like:

- How should responsibilities be split across services?
- When should communication be synchronous vs. asynchronous?
- How does each service maintain ownership of its own data?
- How should inventory be handled when an order is cancelled?

It's a hands-on exploration of microservices, service communication, authentication, event-driven architecture, persistence, and distributed business workflows — not just a collection of CRUD endpoints.