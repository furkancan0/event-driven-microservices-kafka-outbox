# 🧩 Event-Driven Microservices with Kafka, Outbox Pattern & Resilience4j  

A Spring Boot microservices architecture designed for reliability, scalability, and fault tolerance, leveraging Apache Kafka for event-driven communication, the Outbox Pattern for data consistency, and Resilience4j for robust synchronous service calls.  

📨 Outbox Pattern

The Outbox Pattern ensures reliable event publishing between microservices, even in cases of system failure or transaction rollback.  
* Events are first stored in an outbox table within the same transaction as the business data.  
* A scheduled or transactional Event Relay later publishes those events to Kafka.  
* Guarantees atomic database writes and event consistency.  
This pattern removes the risk of lost events and ensures data synchronization between services (e.g., Order → Inventory, Order → Notification).  

🔄 Kafka Event-Driven Architecture  

The system follows an event-driven architecture where microservices communicate asynchronously via Kafka topics.  
* Enables loose coupling between services.  
* Allows asynchronous propagation of events (like order.created, inventory.updated).  
* Facilitates independent scaling and failure isolation.  
Kafka is primarily used for propagating domain events such as:  
* Inventory updates after checkout  
* Notifications or analytics after order processing  

✅ Flow Summary  

1. The Order Service receives a checkout request.  
2. It saves the order data and simultaneously calls the Payment Service via RestClient / RestTemplate.  
3. The communication is protected by Resilience4j CircuitBreaker and Retry, ensuring fault tolerance in case the Payment Service is slow or temporarily unavailable.  
4. If the payment succeeds, the order is confirmed and a success event is published to Kafka via the Outbox Pattern.  
5. Other services (e.g., Inventory, Notification) react to these Kafka events asynchronously.  

🧠 Resilience & Fault Tolerance  
* The project integrates Resilience4j to strengthen service-to-service communication:  
* CircuitBreaker: Detects repeated failures and opens temporarily to prevent cascading issues.  
* Retry: Automatically retries failed calls to handle transient errors.  
* Fallbacks: Ensures graceful degradation when downstream services are unavailable.  
* These patterns are mainly used in the Order → Payment interaction to maintain a smooth checkout experience even under partial system outages.

🧱 Architecture Overview  
Core Components:  
* API Gateway: Entry point for all external requests (routing, authentication, rate limiting)  
* Discovery Server (Eureka): Service registry and dynamic discovery for microservices.  
* Auth Service: Handles user registration, login, and JWT-based authentication.  
* Order Service: Manages orders, initiates payments synchronously, and publishes outbox events.  
* Payment Service: Processes payments upon direct REST request from Order Service.  
* Inventory Service: Updates stock asynchronously via Kafka event consumption.  
* Kafka Broker: Central event bus connecting microservices.

| Communication Type    | Mechanism                 | Usage                                         |  
| --------------------- | ------------------------- | --------------------------------------------- |  
| **Synchronous**       | RestClient / RestTemplate | Order → Payment (with CircuitBreaker & Retry) |  
| **Asynchronous**      | Kafka Events              | Order → Inventory, Order → Notification       |  
| **Service Discovery** | Eureka                    | Dynamic routing & load balancing              |  
| **Gateway Routing**   | Spring Cloud Gateway      | Unified API entry point                       |  















