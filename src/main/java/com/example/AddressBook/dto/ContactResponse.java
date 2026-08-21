package com.example.AddressBook.dto;

public class ContactResponse {
    private Long id;
    private String name;
    private String phone;
    private String addressBookName;

    public ContactResponse() {
    }

    public ContactResponse(Long id, String name, String phone, String addressBookName) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.addressBookName = addressBookName;
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

    public String getAddressBookName() {
        return addressBookName;
    }

    public void setAddressBookName(String addressBookName) {
        this.addressBookName = addressBookName;
    }
}
