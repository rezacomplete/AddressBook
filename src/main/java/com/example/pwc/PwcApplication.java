package com.example.pwc;

import com.example.pwc.model.AddressBook;
import com.example.pwc.model.AddressBooks;
import com.example.pwc.model.Person;
import com.example.pwc.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import java.util.*;

@SpringBootApplication
public class PwcApplication implements CommandLineRunner {

	@Autowired
	AddressBookService addressBookService;

	public static void main(String[] args) {
		SpringApplication.run(PwcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Console console = System.console();

		System.out.println();
		System.out.println();
		while (true) {
			System.out.println("***************************************");
			System.out.println("Please select one of the below options:");
			System.out.println("1) Create address book");
			System.out.println("2) Query address book");
			System.out.println("3) Compare address books");
			System.out.println("4) Delete address books");
			System.out.println("5) Exit");
			System.out.print("? ");

			String option = console.readLine();

			if (option.equalsIgnoreCase("1")) {
				createPhoneBook(console);
			} else if (option.equalsIgnoreCase("2")) {
				showAddressBooks(console);
			} else if (option.equalsIgnoreCase("3")) {
				compareAddressBooks(console);
			} else if (option.equalsIgnoreCase("4")) {
				addressBookService.deleteAddressBooks();
			} else if (option.equalsIgnoreCase("5")) {
				break;
			}
		}
	}

	private void compareAddressBooks(Console console) {
		AddressBooks addressBooks = addressBookService.findAddressBooks();

		if (addressBooks == null || addressBooks.getAddressBooks() == null || addressBooks.getAddressBooks().isEmpty()) {
			return;
		}

		System.out.println("***************************************");
		System.out.println("Select first address book:");

		for (AddressBook addressBook : addressBooks.getAddressBooks()) {
			System.out.println("-" + addressBook.getName());
		}

		System.out.print("? ");
		String firstAddressBookName = console.readLine();

		System.out.println("***************************************");
		System.out.println("Select second address book:");

		for (AddressBook addressBook : addressBooks.getAddressBooks()) {
			if (!addressBook.getName().equalsIgnoreCase(firstAddressBookName)) {
				System.out.println("-" + addressBook.getName());
			}
		}

		System.out.print("? ");
		String secondAddressBookName = console.readLine();

		List<String> names = addressBookService.findAllUniquePersonList(firstAddressBookName, secondAddressBookName, addressBooks);
		for (String name : names) {
			System.out.println("-" + name);
		}
	}

	private void createPhoneBook(Console console) {
		System.out.println("***************************************");
		System.out.print("Address book name: ");
		String addressBookName = console.readLine();

		AddressBook addressBook = new AddressBook();
		addressBook.setName(addressBookName);

		List<Person> personList = new ArrayList<>();
		do {
			System.out.print("Name: ");
			String name = console.readLine();
			System.out.print("Number: ");
			String number = console.readLine();

			Person person = new Person();
			person.setName(name);
			person.setNumber(number);
			personList.add(person);

			System.out.print("Another (y/n): ");

		} while (console.readLine().equalsIgnoreCase("y"));

		addressBook.setPersonList(personList);

		addressBookService.save(addressBook);
	}

	private void showAddressBooks(Console console) {
		AddressBooks addressBooks = addressBookService.findAddressBooks();

		if (addressBooks == null || addressBooks.getAddressBooks() == null || addressBooks.getAddressBooks().isEmpty()) {
			return;
		}

		System.out.println("***************************************");

		for (AddressBook addressBook : addressBooks.getAddressBooks()) {
			System.out.println("-" + addressBook.getName());
		}

		System.out.print("? ");
		String addressBookName = console.readLine();

		for (AddressBook addressBook : addressBooks.getAddressBooks()) {
			if (addressBook.getName().equalsIgnoreCase(addressBookName)) {
				printPersonList(addressBook);
				break;
			}
		}
	}

	private void printPersonList(AddressBook addressBook) {
		System.out.println("***************************************");
		System.out.println("Address book: " + addressBook.getName());

		addressBook.getPersonList().sort(Comparator.comparing(Person::getName));

		for (Person person : addressBook.getPersonList()) {
			System.out.println("-" + person.getName() + "\t" + person.getNumber());
		}
	}
}
