# High-Throughput API Rate Limiter

A distributed, high-performance API Rate Limiter built with Java, Spring Boot, and Redis, designed to protect backend services from overuse.

* **Architecture**: Microservice-based with an API Gateway.
* **Algorithm**: Implements the scalable Token Bucket algorithm.
* **Datastore**: Uses Redis for distributed, high-speed state management.
* **Tech Stack**: Java 21, Spring Boot 3, Spring Cloud Gateway, Redis, Maven, Docker.

## Performance Benchmark
Load-tested with k6 to simulate 100 virtual users. The system correctly enforces limits while adding minimal overhead.

* **Average Latency**: 11.44ms
* **95th Percentile Latency**: 16.01ms
* **Requests per Second**: ~79 req/s

## Quick Start

1. **Prerequisites**: Docker & Git installed.
2. **Clone**: git clone <your-repo-url>
3. **Run Redis**: docker run --name my-redis -p 6379:6379 -d redis
4. **Run Services**: In separate terminals, navigate to api-gateway, rate-limiter-service, and product-service and run ./mvnw spring-boot:run.
5. **Test**: curl http://localhost:8080/api/products

(The rate limit is 10 requests per minute per IP).
