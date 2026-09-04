package com.example.AddressBook.service;

import com.example.AddressBook.dto.AddressBookResponse;
import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;
import com.example.AddressBook.dto.ContactResponseWithId;

import java.util.List;

public interface ContactService {
    ContactResponseWithId createContact(String addressBookName, ContactRequest request);
    List<ContactResponse> listContacts(String addressBookName, int page, int size);
    void deleteContactById(String addressBookName, Long id);
    List<ContactResponse> listUniqueContacts();
    AddressBookResponse addAddressBook(String name);
    void removeAddressBookIfEmpty(String name);
    List<String> listAddressBooks();
}
