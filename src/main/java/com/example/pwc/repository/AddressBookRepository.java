package com.example.pwc.repository;

import com.example.pwc.model.AddressBook;
import com.example.pwc.model.AddressBooks;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Component
public class AddressBookRepository {
    public void save(AddressBooks addressBooks) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("database.binary");
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(addressBooks);
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public AddressBooks findAddressBooks() {
        AddressBooks addressBooks = null;
        try{
            FileInputStream fileInputStream = new FileInputStream("database.binary");
            ObjectInputStream inputStream =  new ObjectInputStream(fileInputStream);
            addressBooks = (AddressBooks)inputStream.readObject();
            inputStream.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return addressBooks;
    }

}
