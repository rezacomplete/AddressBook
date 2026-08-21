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

- HTTP method: POST
- URL: /api/address-books/{addressBookName}/contacts
- Request Content-Type: application/json

Example curl to create a contact in address book `MyBook`:

```bash
curl -v -X POST "http://localhost:8080/api/address-books/MyBook/contacts" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","phone":"+1234567890"}'
```

## Remove a contact by ID

Remove a contact that belongs to a particular address book by its id.

- HTTP method: DELETE
- URL: /api/address-books/{addressBookName}/contacts/{id}


```bash
curl -i -X DELETE "http://localhost:8080/api/address-books/MyBook/contacts/1"
```

## List contacts

Retrieve all contacts in a named address book.

- HTTP method: GET
- URL: /api/address-books/{addressBookName}/contacts

Example curl (replace `{MyBook}`):

```bash
curl -s "http://localhost:8080/api/address-books/MyBook/contacts" | jq
```

Example response (200 OK):

```json
[
  {
    "id": 1,
    "name": "Alice",
    "phone": "+1234567890",
    "addressBookName": "MyBook"
  },
  {
    "id": 2,
    "name": "Bob",
    "phone": "+1987654321",
    "addressBookName": "MyBook"
  }
]
```

## List unique contacts

Retrieve unique contacts across address books. The endpoint returns contact tuples (addressBookName, name, phone) using the `contacts/unique` endpoint.

- HTTP method: GET
- URL: /api/contacts/unique

Example curl:

```bash
curl -s "http://localhost:8080/api/contacts/unique" | jq
```

Example response (200 OK):

```json
[
  {
    "id": null,
    "name": "Alice",
    "phone": "+1234567890",
    "addressBookName": null
  },
  {
    "id": null,
    "name": "Alice",
    "phone": "+1234567890",
    "addressBookName": null
  }
]
```


## Address book operations

### Create an address book

- HTTP method: POST
- URL: /api/address-books
- Request Content-Type: application/json

Example request body:
```json
{
  "name": "MyBook"
}
```

Example curl:

```bash
curl -v -X POST "http://localhost:8080/api/address-books" \
  -H "Content-Type: application/json" \
  -d '{"name":"MyBook"}'
```


### Remove an address book if empty

- HTTP method: DELETE
- URL: /api/address-books/{addressBookName}

Example curl:

```bash
curl -i -X DELETE "http://localhost:8080/api/address-books/MyBook"
```

### List address books

- HTTP method: GET
- URL: /api/address-books

Example curl:

```bash
curl -s "http://localhost:8080/api/address-books" | jq
```

Example response (200 OK):

```json
["MyBook", "OtherBook"]
```

## Running tests

- Run all tests:

```bash
./mvnw test
```

