package model;

import java.io.Serializable; 
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
	private String name;
	private String description;
	private List<Contact> contacts;

	public Group(String name, String description) {
		this.name = name;
		this.description = description;
		this.contacts = new ArrayList<>();
	}

	public void addContact(Contact contact) {
		if (!contacts.contains(contact))
			contacts.add(contact);
	}

	public void removeContact(Contact contact) {
		contacts.remove(contact);
	}

	public void clearContacts() {
		contacts.clear();
	}

	public boolean containsContact(Contact contact) {
		return contacts.contains(contact);
	}

	// Getters and setters
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return name; 
	}
}
