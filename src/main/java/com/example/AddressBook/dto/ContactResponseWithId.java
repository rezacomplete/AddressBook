package com.example.AddressBook.dto;

public class ContactResponseWithId extends ContactResponse {
    private Long id;

    public ContactResponseWithId() {
        super();
    }

    public ContactResponseWithId(Long id, String name, String phone) {
        super(name, phone);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
