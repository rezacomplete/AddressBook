package com.example.pwc.model;

import java.io.Serializable;
import java.util.List;

public class AddressBooks implements Serializable {
    private List<AddressBook> addressBooks;

    public List<AddressBook> getAddressBooks() {
        return addressBooks;
    }

    public void setAddressBooks(List<AddressBook> addressBooks) {
        this.addressBooks = addressBooks;
    }
}
