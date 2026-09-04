package com.example.AddressBook.service;

import com.example.AddressBook.dto.ContactRequest;
import com.example.AddressBook.dto.ContactResponse;
import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.model.Contact;
import com.example.AddressBook.repository.AddressBookRepository;
import com.example.AddressBook.repository.ContactRepository;
import com.example.AddressBook.repository.UniqueContactProjection;
import com.example.AddressBook.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    private AddressBookRepository addressBookRepository;
    private ContactRepository contactRepository;
    private ContactService service;

    @BeforeEach
    void setUp() {
        addressBookRepository = mock(AddressBookRepository.class);
        contactRepository = mock(ContactRepository.class);
        service = new ContactServiceImpl(addressBookRepository, contactRepository);
    }

    @Test
    void testCreateContact_NoDuplicates() {
        String addressBookName = "Friends";

        ContactRequest request = new ContactRequest();
        request.setName("John Doe");
        request.setPhone("1234567890");

        Contact contact = new Contact(request.getName(), request.getPhone());

        AddressBook addressBook = new AddressBook(addressBookName);
        when(addressBookRepository.findByName(addressBookName)).thenReturn(Optional.of(addressBook));
        when(contactRepository.findByAddressBook_NameAndNameAndPhone(addressBookName, request.getName(), request.getPhone()))
                .thenReturn(Optional.of(contact));

        ContactResponse result = service.createContact(addressBookName, request);

        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getPhone()).isEqualTo("1234567890");
    }

    @Test
    void testCreateContact_Duplicate() {
        String addressBookName = "Friends";

        ContactRequest request = new ContactRequest();
        request.setName("John Doe");
        request.setPhone("1234567890");

        Contact contact = new Contact(request.getName(), request.getPhone());

        AddressBook addressBook = new AddressBook(addressBookName);
        addressBook.addContact(contact);

        when(addressBookRepository.findByName(addressBookName)).thenReturn(Optional.of(addressBook));

        assertThrows(RuntimeException.class, () -> service.createContact(addressBookName, request));
    }

    @Test
    void testDeleteContactById_Success() {
        String addressBookName = "Friends";
        Long contactId = 1L;

        Contact contact = new Contact("John Doe", "1234567890");
        when(contactRepository.findByIdAndAddressBook_Name(contactId, addressBookName)).thenReturn(Optional.of(contact));

        service.deleteContactById(addressBookName, contactId);

        verify(contactRepository, times(1)).delete(contact);
    }

    @Test
    void testDeleteContactById_NotFound() {
        String addressBookName = "Friends";
        Long contactId = 1L;

        when(contactRepository.findByIdAndAddressBook_Name(contactId, addressBookName)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteContactById(addressBookName, contactId));
    }

    @Test
    void testListUniqueContacts() {
        Contact contact1 = new Contact("John Doe", "1234567890");
        Contact contact2 = new Contact("Jane Smith", "0987654321");

        UniqueContactProjection projection1 = new UniqueContactProjection() {
            @Override
            public String getName() {
                return contact1.getName();
            }

            @Override
            public String getPhone() {
                return contact1.getPhone();
            }
        };

        UniqueContactProjection projection2 = new UniqueContactProjection() {
            @Override
            public String getName() {
                return contact2.getName();
            }

            @Override
            public String getPhone() {
                return contact2.getPhone();
            }
        };

        when(contactRepository.findUniqueContacts(isA(Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(projection1, projection2)));

        Page<ContactResponse> result = service.listUniqueContacts(1, 10);
        List<ContactResponse> content = result.getContent();

        assertThat(result).hasSize(2);
        assertThat(content.getFirst().getName()).isEqualTo("John Doe");
        assertThat(content.get(1).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void testAddAddressBook_CreatesWhenNotExists() {
        String name = "Work";
        when(addressBookRepository.existsByName(name)).thenReturn(false);
        AddressBook saved = new AddressBook(name);
        saved.setId(42L);
        when(addressBookRepository.save(any())).thenReturn(saved);

        var resp = service.addAddressBook(name);

        assertThat(resp.getId()).isEqualTo(42L);
        assertThat(resp.getName()).isEqualTo(name);
    }

    @Test
    void testRemoveAddressBookIfEmpty_Success() {
        String name = "EmptyBook";
        AddressBook book = new AddressBook(name);
        when(addressBookRepository.findByName(name)).thenReturn(Optional.of(book));

        service.removeAddressBookIfEmpty(name);

        verify(addressBookRepository, times(1)).delete(book);
    }

    @Test
    void testRemoveAddressBookIfEmpty_NotEmpty() {
        String name = "NonEmpty";
        AddressBook book = new AddressBook(name);
        book.addContact(new Contact("X","1"));
        when(addressBookRepository.findByName(name)).thenReturn(Optional.of(book));

        assertThrows(RuntimeException.class, () -> service.removeAddressBookIfEmpty(name));
    }

    @Test
    void testListAddressBooks() {
        AddressBook a = new AddressBook("A");
        AddressBook b = new AddressBook("B");
        when(addressBookRepository.findAll()).thenReturn(java.util.List.of(a, b));

        java.util.List<String> result = service.listAddressBooks();

        assertThat(result).containsExactly("A", "B");
    }

}
