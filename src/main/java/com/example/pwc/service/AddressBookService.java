package com.example.pwc.service;

import com.example.pwc.model.AddressBook;
import com.example.pwc.model.AddressBooks;
import com.example.pwc.model.Person;
import com.example.pwc.repository.AddressBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AddressBookService {

    @Autowired
    AddressBookRepository addressBookRepository;

    public void save(AddressBook addressBook) {
        AddressBooks addressBooks = addressBookRepository.findAddressBooks();

        if (addressBooks == null) {
            addressBooks = new AddressBooks();
            addressBooks.setAddressBooks(new ArrayList<>());
        }

        List<AddressBook> addressBookList = addressBooks.getAddressBooks();
        addressBookList.add(addressBook);
        addressBooks.setAddressBooks(addressBookList);

        addressBookRepository.save(addressBooks);
    }

    public AddressBooks findAddressBooks() {
        return addressBookRepository.findAddressBooks();
    }

    public List<String> findAllUniquePersonList(String firstAddressBookName, String secondAddressBookName, AddressBooks addressBooks) {
        Set<String> firstAddressBookNameSet = new HashSet<>();
        Set<String> secondAddressBookNameSet = new HashSet<>();

        List<String> uniqueNames = new ArrayList<>();

        AddressBook firstAddressBook = findAddressBook(firstAddressBookName, addressBooks);
        AddressBook secondAddressBook = findAddressBook(secondAddressBookName, addressBooks);

        if (firstAddressBook != null) {
            for (Person person : firstAddressBook.getPersonList()) {
                firstAddressBookNameSet.add(person.getName());
            }
        }

        if (secondAddressBook != null) {
            for(Person person : secondAddressBook.getPersonList()) {
                secondAddressBookNameSet.add(person.getName());
            }
        }

        if (firstAddressBook != null) {
            for (Person person : firstAddressBook.getPersonList()) {
                if (!secondAddressBookNameSet.contains(person.getName())) {
                    uniqueNames.add(person.getName());
                }
            }
        }

        if (secondAddressBook != null) {
            for(Person person : secondAddressBook.getPersonList()) {
                if (!firstAddressBookNameSet.contains(person.getName())) {
                    uniqueNames.add(person.getName());
                }
            }
        }

        return uniqueNames;
    }

    private AddressBook findAddressBook(String addressBookName, AddressBooks addressBooks) {
        for (AddressBook addressBook : addressBooks.getAddressBooks()) {
            if (addressBook.getName().equalsIgnoreCase(addressBookName)) {
                return addressBook;
            }
        }

        return null;
    }

    public void deleteAddressBooks() {
        AddressBooks addressBooks = addressBookRepository.findAddressBooks();
        if (addressBooks != null) {
            addressBooks.setAddressBooks(new ArrayList<>());
            addressBookRepository.save(addressBooks);
        }
    }
}
