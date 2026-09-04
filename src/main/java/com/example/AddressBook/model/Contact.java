package com.example.AddressBook.model;

import jakarta.persistence.*;

@Entity
//@Table(name = "contact", indexes = {@Index(columnList = "name", name = "idx_contact_name"), @Index(columnList = "phone", name = "idx_contact_phone")})
@Table(name = "contact", indexes = {
//        @Index(name = "idx_contact_name", columnList = "name"),
//        @Index(name = "idx_contact_phone", columnList = "phone"),
        @Index(name = "idx_contact_name_phone", columnList = "name, phone")
})
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id", nullable = false)
    private AddressBook addressBook;

    public Contact() {
    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressBook getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }
}
