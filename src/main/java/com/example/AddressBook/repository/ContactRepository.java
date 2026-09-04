package com.example.AddressBook.repository;

import com.example.AddressBook.model.Contact;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByIdAndAddressBook_Name(Long id, String addressBookName);
    Optional<Contact> findByAddressBook_NameAndNameAndPhone(String addressBookName, String name, String phone);
    Page<Contact> findByAddressBookName(String addressBookName, Pageable pageable);

    @Query("SELECT c.name as name, c.phone as phone FROM Contact c GROUP BY c.name, c.phone")
    List<UniqueContactProjection> findUniqueContacts();

    // page through unique (name, phone) pairs efficiently
    @Query("SELECT DISTINCT c.name as name, c.phone as phone FROM Contact c")
    Page<UniqueContactProjection> findUniqueContacts(Pageable pageable);

    // stream unique (name, phone) pairs for large datasets; keep transaction open while streaming
    @Transactional(readOnly = true)
    @Query("SELECT DISTINCT c.name as name, c.phone as phone FROM Contact c")
    @QueryHints({
            @QueryHint(name = "org.hibernate.fetchSize", value = "500"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true")
    })
    Stream<UniqueContactProjection> streamUniqueContacts();
}
