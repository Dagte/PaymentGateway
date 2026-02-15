# Instructions for candidates

This is the Java version of the Payment Gateway challenge. If you haven't already read this [README.md](https://github.com/cko-recruitment/) on the details of this exercise, please do so now.

## Requirements
- JDK 17
- Docker

## Template structure

src/ - A skeleton SpringBoot Application

test/ - Some simple JUnit tests

imposters/ - contains the bank simulator configuration. Don't change this

.editorconfig - don't change this. It ensures a consistent set of rules for submissions when reformatting code

docker-compose.yml - configures the bank simulator


## API Documentation
For documentation openAPI is included, and it can be found under the following url: **http://localhost:8090/swagger-ui/index.html**

**Feel free to change the structure of the solution, use a different library etc.**

# Notes from candidate

## About project organization
I decided to restructure the project slightly based in concepts from hexagonal architecture:
 - api: here we keep the controller HTTP requests, some mappings, validation and dtos
 - common: contains generic concepts shared across the project
 - core: contains the business logic and handles the payment flow and domain related logic, it checks idempotency, calls the bank, and updates the status
 - infrastructure: it contains the necessary classes to communicate with the outside world (adapters) 

## About handling concurrency and idempotency
To prevent users from being charged twice, I implemented two layers of protection:
 - I introduced a header "Idempotency-Key" which client would be expected to send and would help us distinguish repeated requests
 - I moved from HashMap<UUID, PostPaymentResponse> to ConcurrentHashMap<UUID, Payment> and introduced a second ConcurrentHashMap which stores and keep track of those idempotency keys
 - I used computeIfAbsent to check for duplicate requests instantly because it guarantee atomicity
 - Then a synchronized block prevents two processes from simultaneously going to the bank with the same payment.

Note: This implementation is made thinking on a single server. In a distributed system, we would need to change this implementation to a distributed lock like Redis.

## About security
- I implemented so that full card numbers or CVVs are not stored, logged or sent back to client.
- Card numbers and CVVs are passed separately from the main Payment object and keep like that to make sure they are not accidentally logged or persisted
- Mappers and differenciated objects in different layers make sure that unneeded internal information is not exposed outside the system

## About resilience
- I added Retries and CircuitBreaker from Resilience4j to handle situations when the bank's API is slow or down. Configuration should be optimised
- I tried to standarise errors All errors return a consistent format, making it easy for merchants to understand why a payment was rejected.
