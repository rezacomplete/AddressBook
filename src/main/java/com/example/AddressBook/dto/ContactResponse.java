package com.example.AddressBook.dto;

public class ContactResponse {
    private String name;
    private String phone;

    public ContactResponse() {
    }

    public ContactResponse(String name, String phone) {
        this.name = name;
        this.phone = phone;
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

}
