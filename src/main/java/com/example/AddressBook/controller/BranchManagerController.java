package com.example.AddressBook.controller;

import com.example.AddressBook.dto.*;
import com.example.AddressBook.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.locks.Lock;

@RestController
@RequestMapping("/api")
public class BranchManagerController {

    private final ContactService contactService;
    private final JdbcLockRegistry lockRegistry;

    public BranchManagerController(ContactService contactService, JdbcLockRegistry lockRegistry) {
        this.contactService = contactService;
        this.lockRegistry = lockRegistry;
    }

    /**
     *  Add new contact entries in the specified address book.
     */
    @PostMapping("/address-books/{addressBookName}/contacts")
    public ResponseEntity<ContactResponseWithId> createContact(@PathVariable String addressBookName,
                                                         @Valid @RequestBody ContactRequest request,
                                                         UriComponentsBuilder uriBuilder) {
        Lock lock = lockRegistry.obtain(addressBookName);
        lock.lock();

        try {
            ContactResponseWithId created = contactService.createContact(addressBookName, request);
            URI location = uriBuilder.path("/api/address-books/{addressBookName}/contacts/{id}").buildAndExpand(addressBookName, created.getId()).toUri();
            return ResponseEntity.created(location).body(created);

        } finally {
            lock.unlock();
        }
    }

    @PostMapping("/address-books")
    public ResponseEntity<AddressBookResponse> createAddressBook(@Valid @RequestBody AddressBookRequest request, UriComponentsBuilder uriBuilder) {
        AddressBookResponse response = contactService.addAddressBook(request.getName());
        URI location = uriBuilder.path("/api/address-books/{name}").buildAndExpand(response.getName()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/address-books/{addressBookName}")
    public ResponseEntity<Void> deleteAddressBookIfEmpty(@PathVariable String addressBookName) {
        contactService.removeAddressBookIfEmpty(addressBookName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/address-books")
    public ResponseEntity<List<String>> listAddressBooks() {
        List<String> list = contactService.listAddressBooks();
        return ResponseEntity.ok(list);
    }


    @DeleteMapping("/address-books/{addressBookName}/contacts/{id}")
    public ResponseEntity<Void> deleteContactById(@PathVariable String addressBookName, @PathVariable Long id) {
        contactService.deleteContactById(addressBookName, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/address-books/{addressBookName}/contacts")
    public ResponseEntity<List<ContactResponse>> listContacts(@PathVariable String addressBookName, @RequestParam int page, @RequestParam int size) {
        List<ContactResponse> list = contactService.listContacts(addressBookName, page, size);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/contacts/unique")
    public ResponseEntity<Page<ContactResponse>> listUniqueContacts(@RequestParam int page, @RequestParam int size) {
        Page<ContactResponse> uniques = contactService.listUniqueContacts(page, size);
        return ResponseEntity.ok(uniques);
    }
}
