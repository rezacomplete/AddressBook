package com.example.AddressBook.service.impl;

import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;
import com.example.AddressBook.dto.AddressBookResponse;
import com.example.AddressBook.dto.ContactResponseWithId;
import com.example.AddressBook.exception.DuplicateContactException;
import com.example.AddressBook.exception.NotFoundException;
import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.model.Contact;
import com.example.AddressBook.repository.AddressBookRepository;
import com.example.AddressBook.repository.ContactRepository;
import com.example.AddressBook.repository.UniqueContactProjection;
import com.example.AddressBook.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public ContactResponseWithId createContact(String addressBookName, ContactRequest request) {
        AddressBook addressBook = addressBookRepository.findByName(addressBookName)
                .orElseGet(() -> addressBookRepository.save(new AddressBook(addressBookName)));

        // check for duplicate contact in the same address book. This is to provide a user-friendly error message instead of relying on the database unique constraint violation
        boolean duplicate =
                contactRepository.existsByAddressBook_NameAndNameAndPhone(
                        addressBookName,
                        request.getName(),
                        request.getPhone()
                );

        if (duplicate) {
            throw new DuplicateContactException(
                    "Contact already exists in the address book"
            );
        }

        Contact contact = new Contact(request.getName(), request.getPhone());
        addressBook.addContact(contact);
        contactRepository.save(contact);

        return new ContactResponseWithId(contact.getId(), contact.getName(), contact.getPhone());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> listContacts(String addressBookName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return contactRepository.findByAddressBookName(addressBookName, pageable).stream()
                .map(p -> new ContactResponse(p.getName(), p.getPhone()))
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

    // streaming call - keep transaction open while consuming the stream
    @Transactional(readOnly = true)
    public List<ContactResponse> streamUniqueContactsAsList() {
        try (Stream<UniqueContactProjection> stream = contactRepository.streamUniqueContacts()) {
            return stream
                    .map(r -> new ContactResponse(r.getName(), r.getPhone()))
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> listUniqueContacts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending().and(Sort.by("phone").ascending()));
        Page<UniqueContactProjection> uniques = contactRepository.findUniqueContacts(pageable); // calls Page<...>
        return uniques.map(p -> new ContactResponse(p.getName(), p.getPhone()));
    }
}
