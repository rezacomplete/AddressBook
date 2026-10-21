# AddressBook

Simple Spring Boot application that manages address books and contacts.

## Prerequisites

- Java 21 (configured in the project `pom.xml`)
- Maven (or use the included Maven wrapper: `./mvnw`)

## Build and run

- Build the project:

```bash
./mvnw clean package
```

- Run the application:

```bash
./mvnw spring-boot:run
```

The app starts by default on port 8080.

## The createContact endpoint

```bash
curl -v -X POST "http://localhost:8080/api/address-books/MyBook/contacts" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","phone":"+1234567890"}'
```

## Remove a contact by ID

```bash
curl -i -X DELETE "http://localhost:8080/api/address-books/MyBook/contacts/1"
```

## List contacts

```bash
curl -s "http://localhost:8080/api/address-books/MyBook/contacts?page=0&size=10" | jq
```


## List unique contacts

```bash
curl -s "http://localhost:8080/api/contacts/unique?page=0&size=10" | jq
```


## Address book operations

### Create an address book

```bash
curl -v -X POST "http://localhost:8080/api/address-books" \
  -H "Content-Type: application/json" \
  -d '{"name":"MyBook"}'
```


### Remove an address book if empty

```bash
curl -i -X DELETE "http://localhost:8080/api/address-books/MyBook"
```

### List address books

```bash
curl -s "http://localhost:8080/api/address-books" | jq
```


## Running tests

- Run all tests:

```bash
./mvnw test
```

## Deployment (Docker-only)

Build the Docker image:

```bash
docker build -t addressbook:local .
```

Run the image:

```bash
docker run --rm -p 8080:8080 addressbook:local
```
