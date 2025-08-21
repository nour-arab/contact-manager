package controller;

import model.Contact;    
import model.ContactManager;
import model.Group;
import utils.FileManager;

import java.util.List;

public class GroupController {
    private ContactManager manager;

    public GroupController(ContactManager manager) {
        this.manager = manager;
    }

    public void addGroup(Group group) {
        manager.addGroup(group);
        FileManager.saveGroups(manager.getGroups());
    }

    public void removeGroup(Group group) {
        manager.removeGroup(group);
        FileManager.saveGroups(manager.getGroups());
    }

    public void updateGroup(Group group) {
    	
        FileManager.saveGroups(manager.getGroups());
    }

    public List<Group> getAllGroups() {
        return manager.getGroups();
    }

    public List<Contact> getContactsInGroup(Group group) {
        return group.getContacts();
    }

    public void addContactToGroup(Contact contact, Group group) {
        group.addContact(contact);
        FileManager.saveGroups(manager.getGroups());
    }

    public void removeContactFromGroup(Contact contact, Group group) {
        group.removeContact(contact);
        FileManager.saveGroups(manager.getGroups());
    }

    public List<Contact> getAllContacts() {
        return manager.getContacts();
    }
    public ContactManager getManager() {
        return manager;
    }
}
