package com.example.AddressBook.demo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.AddressBook.model.AddressBook;
import com.example.AddressBook.model.Contact;
import com.example.AddressBook.repository.AddressBookRepository;
import com.example.AddressBook.repository.ContactRepository;

@Component 
@Profile("testdata")
public class BookDataLoader {

  private final AddressBookRepository addressBookRepository;
  private final ContactRepository contactRepository;

  public BookDataLoader(AddressBookRepository addressBookRepository, ContactRepository contactRepository) {
    this.addressBookRepository = addressBookRepository;
    this.contactRepository = contactRepository;
  }

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void loadBookTestData() {
    String bookName = "MyBook";
    AddressBook addressBook = addressBookRepository.findByName(bookName)
        .orElseGet(() -> addressBookRepository.save(new AddressBook(bookName)));

    for (int number = 1; number <= 10; number++) {
      String name = "Friend " + number;
      String phone = "202-555-01%02d".formatted(number);
      if (!contactRepository.existsByAddressBook_NameAndNameAndPhone(bookName, name, phone)) {
        Contact contact = new Contact(name, phone);
        addressBook.addContact(contact);
        contactRepository.save(contact);
      }
    }
  }
}
