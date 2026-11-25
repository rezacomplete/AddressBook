# AddressBook

Simple Spring Boot application that manages books.

## Prerequisites

- Java 21 (configured in the project `pom.xml`)
- Maven (or use the included Maven wrapper: `./mvnw`)

## Build and run

- Build the project:

```bash
./mvnw clean package
```

To build the project without running tests, use the Maven wrapper.

```sh
./mvnw clean package -DskipTests
```

- Run the application:

```bash
./mvnw spring-boot:run
```

The app starts by default on port 8080.

## Add a book

```bash
curl -i -X POST 'http://localhost:8080/books' \
  -H 'Content-Type: application/json' \
  -d '{"isbn":"9780140328721","title":"Matilda","author":"Roald Dahl","price":12.99,"version":0}'
```

The ISBN must contain 10 or 13 digits, the title and author must be nonblank, and the price must be greater than zero. A successful request returns `201 Created` and the saved book as JSON.

## Running tests

- Run all tests:

```bash
./mvnw test
```

## Deployment (Docker-only)

Build the Docker image:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=address-book:local
```

Run the image:

```bash
docker run --rm -p 8080:8080 address-book:local
```
