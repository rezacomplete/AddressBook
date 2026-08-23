package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByAddressBook_Name(String addressBookName);
    Optional<Contact> findByIdAndAddressBook_Name(Long id, String addressBookName);
    Optional<Contact> findByAddressBook_NameAndNameAndPhone(String addressBookName, String name, String phone);

    @Query("SELECT c.name as name, c.phone as phone FROM Contact c GROUP BY c.name, c.phone")
    List<UniqueContactProjection> findUniqueContacts();
}
