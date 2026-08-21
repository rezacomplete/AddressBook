package com.example.AddressBook.service;

import com.example.AddressBook.dto.AddressBookResponse;
import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;

import java.util.List;

public interface ContactService {
    ContactResponse createContact(String addressBookName, ContactRequest request);
    List<ContactResponse> listContacts(String addressBookName);
    void deleteContactById(String addressBookName, Long id);
    List<ContactResponse> listUniqueContacts();
    AddressBookResponse addAddressBook(String name);
    void removeAddressBookIfEmpty(String name);
    List<String> listAddressBooks();
}
