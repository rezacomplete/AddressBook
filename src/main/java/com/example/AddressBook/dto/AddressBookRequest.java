package com.example.AddressBook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressBookRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    public AddressBookRequest() {
    }

    public AddressBookRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
