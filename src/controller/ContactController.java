package controller;

import model.Contact;   
import model.ContactManager;
import utils.FileManager;
import model.Group;
import model.PhoneNumber;

import java.util.List;

public class ContactController {
    private ContactManager manager;

    public ContactController(ContactManager manager) {
        this.manager = manager;
    }

    public void addContact(Contact contact) {
        
        
            FileManager.saveContacts(manager.getContacts());
            FileManager.saveGroups(manager.getGroups());
        
        
    }

    public void removeContact(Contact contact) {
       
        FileManager.saveContacts(manager.getContacts());
    }

    public boolean updateContact(Contact originalContact, Contact updatedContact, List<Group> selectedGroups) {
    	manager.updateContact(originalContact, updatedContact, selectedGroups);
        // Save both contacts and groups to persist changes
        FileManager.saveContacts(manager.getContacts());
        FileManager.saveGroups(manager.getGroups());

        // Removed: manager.notifyContactListeners();

        return true;
    }
        
    public List<Contact> getContactsSortedByFirstName() {
        manager.sortByFirstName();
        return manager.getContacts();
    }

    public List<Contact> getContactsSortedByLastName() {
        manager.sortByLastName();
        return manager.getContacts();
    }

    public List<Contact> getContactsSortedByCity() {
        manager.sortByCity();
        return manager.getContacts();
    }

    public List<Contact> searchContacts(String prefix) {
        return manager.searchContacts(prefix);
    }

    public List<Group> getAllGroups() {
        return manager.getGroups();
    }
    
    public List<Contact> getContacts(){
        return manager.getContacts();
    }
    
    public ContactManager getManager() {
        return manager;
    }
}
