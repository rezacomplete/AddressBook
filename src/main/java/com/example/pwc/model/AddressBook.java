package com.example.pwc.model;

import java.io.Serializable;
import java.util.List;

public class AddressBook implements Serializable {
    private String name;
    private List<Person> personList;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }
}
