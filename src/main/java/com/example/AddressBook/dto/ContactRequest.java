package com.example.AddressBook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public class ContactRequest {

    @NotBlank(message = "name must be provided")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "phone must be provided")
    @Size(max = 50)
    @Pattern(regexp = "^\\+?[0-9 .-]{7,20}$", message = "phone must be a valid phone number")
    private String phone;

    public ContactRequest() {
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
