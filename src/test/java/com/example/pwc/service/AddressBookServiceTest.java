package com.example.pwc.service;

import com.example.pwc.PwcApplication;
import com.example.pwc.model.AddressBook;
import com.example.pwc.model.AddressBooks;
import com.example.pwc.model.Person;
import com.example.pwc.repository.AddressBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PwcApplication.class },
        initializers = ConfigFileApplicationContextInitializer.class)
class AddressBookServiceTest {

    @Mock
    AddressBookRepository addressBookRepository;

    AddressBookService addressBookService;

    @BeforeEach
    void setUp() {
        this.addressBookService = new AddressBookService();
        this.addressBookService.addressBookRepository = addressBookRepository;
    }

    @Test
    void save() {
        AddressBook addressBook = new AddressBook();
        addressBookService.save(addressBook);
        verify(addressBookRepository, times(1)).findAddressBooks();
        verify(addressBookRepository, times(1)).save(isA(AddressBooks.class));
    }

    @Test
    void findAddressBooks() {
        AddressBooks addressBooks = new AddressBooks();
        when(addressBookRepository.findAddressBooks()).thenReturn(addressBooks);
        AddressBooks result = addressBookService.findAddressBooks();
        assertEquals(addressBooks, result);
    }

    @Test
    void findAllUniquePersonList() {
        Person bob = new Person();
        bob.setName("Bob");

        Person mary = new Person();
        mary.setName("Mary");

        Person jane = new Person();
        jane.setName("Jane");

        Person john = new Person();
        john.setName("John");

        AddressBook book1 = new AddressBook();
        book1.setPersonList(Arrays.asList(bob, mary, jane));
        book1.setName("Book1");

        AddressBook book2 = new AddressBook();
        book2.setPersonList(Arrays.asList(mary, john, jane));
        book2.setName("Book2");

        AddressBooks addressBooks = new AddressBooks();
        addressBooks.setAddressBooks(Arrays.asList(book1, book2));

        List<String> result = addressBookService.findAllUniquePersonList("Book1", "Book2", addressBooks);

        assertEquals(2, result.size());
    }

    @Test
    void deleteAddressBooks() {
        AddressBooks addressBooks = new AddressBooks();
        when(addressBookRepository.findAddressBooks()).thenReturn(addressBooks);
        addressBookService.deleteAddressBooks();
        verify(addressBookRepository, times(1)).save(addressBooks);
    }
}