package com.example.AddressBook.repository;

import com.example.AddressBook.model.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {
    Optional<AddressBook> findByName(String name);
    boolean existsByName(String name);
}
