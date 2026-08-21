package com.example.AddressBook.controller;

import com.example.AddressBook.dto.AddressBookRequest;
import com.example.AddressBook.dto.AddressBookResponse;
import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;
import com.example.AddressBook.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BranchManagerController {

    private final ContactService contactService;

    public BranchManagerController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     *  Add new contact entries in the specified address book.
     */
    @PostMapping("/address-books/{addressBookName}/contacts")
    public ResponseEntity<ContactResponse> createContact(@PathVariable String addressBookName,
                                                         @Valid @RequestBody ContactRequest request,
                                                         UriComponentsBuilder uriBuilder) {
        ContactResponse created = contactService.createContact(addressBookName, request);
        URI location = uriBuilder.path("/api/address-books/{addressBookName}/contacts/{id}")
                .buildAndExpand(addressBookName, created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
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

    @GetMapping("/address-books/{addressBookName}/contacts")
    public ResponseEntity<List<ContactResponse>> listContacts(@PathVariable String addressBookName) {
        List<ContactResponse> list = contactService.listContacts(addressBookName);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/contacts/unique")
    public ResponseEntity<List<ContactResponse>> listUniqueContacts() {
        List<ContactResponse> uniques = contactService.listUniqueContacts();
        return ResponseEntity.ok(uniques);
    }
}
