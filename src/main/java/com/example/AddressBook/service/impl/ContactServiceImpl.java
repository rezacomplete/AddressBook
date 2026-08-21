package com.example.AddressBook.service.impl;

import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;
import com.example.AddressBook.dto.AddressBookResponse;
import com.example.AddressBook.exception.DuplicateContactException;
import com.example.AddressBook.exception.NotFoundException;
import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.model.Contact;
import com.example.AddressBook.repository.AddressBookRepository;
import com.example.AddressBook.repository.ContactRepository;
import com.example.AddressBook.repository.UniqueContactProjection;
import com.example.AddressBook.service.ContactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    private final AddressBookRepository addressBookRepository;
    private final ContactRepository contactRepository;

    public ContactServiceImpl(AddressBookRepository addressBookRepository, ContactRepository contactRepository) {
        this.addressBookRepository = addressBookRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    @Transactional
    public ContactResponse createContact(String addressBookName, ContactRequest request) {
        AddressBook book = addressBookRepository.findByName(addressBookName)
                .orElseGet(() -> addressBookRepository.save(new AddressBook(addressBookName)));

        // check duplicates within the same address book
        boolean duplicate = book.getContacts().stream().anyMatch(c ->
                request.getName().equalsIgnoreCase(c.getName()) && request.getPhone().equalsIgnoreCase(c.getPhone()));

        if (duplicate) {
            throw new DuplicateContactException("Contact already exists in the address book");
        }

        Contact contact = new Contact(request.getName(), request.getPhone());
        book.addContact(contact);
        addressBookRepository.save(book);
        // ensure pending inserts are flushed so the following query can find the persisted contact and its generated id
        contactRepository.flush();

        // query the persisted contact by address book, name and phone to obtain the generated id
        Contact saved = contactRepository.findByAddressBook_NameAndNameAndPhone(addressBookName, request.getName(), request.getPhone())
                .orElse(contact);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> listContacts(String addressBookName) {
        return contactRepository.findByAddressBook_Name(addressBookName).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteContactById(String addressBookName, Long id) {
        Contact contact = contactRepository.findByIdAndAddressBook_Name(id, addressBookName)
                .orElseThrow(() -> new NotFoundException("Contact not found in address book"));
        contactRepository.delete(contact);
    }

    @Override
    @Transactional
    public AddressBookResponse addAddressBook(String name) {
        if (addressBookRepository.existsByName(name)) {
            // return existing address book info if exists
            return addressBookRepository.findByName(name)
                    .map(b -> new AddressBookResponse(b.getId(), b.getName()))
                    .orElse(new AddressBookResponse(null, name));
        }
        AddressBook saved = addressBookRepository.save(new AddressBook(name));
        return new AddressBookResponse(saved.getId(), saved.getName());
    }

    @Override
    @Transactional
    public void removeAddressBookIfEmpty(String name) {
        AddressBook book = addressBookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Address book not found"));
        if (!book.getContacts().isEmpty()) {
            throw new RuntimeException("Address book is not empty");
        }
        addressBookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listAddressBooks() {
        return addressBookRepository.findAll().stream().map(AddressBook::getName).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> listUniqueContacts() {
        List<UniqueContactProjection> uniques = contactRepository.findUniqueContacts();
        return uniques.stream().map(p -> new ContactResponse(null, p.getName(), p.getPhone(), null)).collect(Collectors.toList());
    }

    private ContactResponse toResponse(Contact contact) {
        String bookName = contact.getAddressBook() != null ? contact.getAddressBook().getName() : null;
        return new ContactResponse(contact.getId(), contact.getName(), contact.getPhone(), bookName);
    }

}
