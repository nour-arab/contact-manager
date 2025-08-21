package model;
 
import java.util.ArrayList;            
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

import utils.FileManager;

public class ContactManager extends Observable {
    private List<Contact> contacts;
    private List<Group> groups;

    public ContactManager() {
        this.contacts = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    public ContactManager(List<Contact> contacts, List<Group> groups) {
        this.contacts = contacts != null ? contacts : new ArrayList<>();
        this.groups = groups != null ? groups : new ArrayList<>();
    }

    public boolean addContact(Contact contact) {
        // Check for duplicate contact name
        for (Contact existingContact : contacts) {
            if (existingContact.getFirstName().equals(contact.getFirstName()) && 
                existingContact.getLastName().equals(contact.getLastName())) {
                return false; // Duplicate name found, reject add
            }
            
            // Check for duplicate phone numbers across existing contacts
            for (PhoneNumber newPhone : contact.getPhoneNumbers()) {
                for (PhoneNumber existingPhone : existingContact.getPhoneNumbers()) {
                    if (newPhone.getRegionCode() == existingPhone.getRegionCode() && 
                        newPhone.getNumber() == existingPhone.getNumber()) {
                        return false; // Duplicate phone number found, reject add
                    }
                }
            }
        }
        
        // No duplicates found, add contact
        contacts.add(contact);
        FileManager.saveContacts(getContacts());
        FileManager.saveGroups(getGroups());
        setChanged();  // Mark this Observable as having been changed
        notifyObservers();  // Notify observers with event and payload
        return true;
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        // Remove contact from all groups it belongs to
        for (Group group : groups) {
            group.removeContact(contact);
        }
        FileManager.saveContacts(getContacts());
        FileManager.saveGroups(getGroups());
        setChanged();
        notifyObservers();  // Notify observers contact removed
    }

    public void addGroup(Group group) {
        if (!groups.contains(group)) { 
             groups.add(group);
             FileManager.saveContacts(getContacts());
             FileManager.saveGroups(getGroups());
             setChanged();
             notifyObservers();  // Notify observers group added
        }
    }

    public void removeGroup(Group group) {
        boolean removed = groups.remove(group);
        if (removed) {
        	FileManager.saveContacts(getContacts());
            FileManager.saveGroups(getGroups());
        	setChanged();
            notifyObservers();  // Notify observers group removed
        }
    }
    
    public void removeContactFromGroup(Contact contact, Group group) {
        group.removeContact(contact);
        FileManager.saveContacts(getContacts());
        FileManager.saveGroups(getGroups());
        setChanged();
        notifyObservers();  // Notify group membership changed
    }

    public List<Contact> getContacts() { return contacts; }
    public List<Group> getGroups() { return groups; }

    public List<Contact> searchContacts(String prefix) {
        List<Contact> results = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getFirstName().toLowerCase().startsWith(prefix.toLowerCase()) ||
                c.getLastName().toLowerCase().startsWith(prefix.toLowerCase())) {
                results.add(c);
            }
        }
        return results;
    }

    public void sortByFirstName() {
        contacts.sort(Comparator.comparing(Contact::getFirstName));
    }

    public void sortByLastName() {
        contacts.sort(Comparator.comparing(Contact::getLastName));
    }

    public void sortByCity() {
        contacts.sort(Comparator.comparing(Contact::getCity));
    }

    // Notify observers when group membership changes externally
    public void groupMembershipChanged() {
    	FileManager.saveContacts(getContacts());
        FileManager.saveGroups(getGroups());
    	setChanged();
        notifyObservers();
    }

    // Notify observers when group details change externally
    public void groupDetailsChanged() {
    	FileManager.saveContacts(getContacts());
        FileManager.saveGroups(getGroups());
    	setChanged();
        notifyObservers();
    }
    public List<String> getGroupNamesContainingContact(Contact contact) {
        List<String> groupNames = new ArrayList<>();
        for (Group group : groups) {
            if (group.containsContact(contact)) {
                groupNames.add(group.getName());
            }
        }
        return groupNames;
    }
    
    public List<Contact> getContactsSortedByFirstName() {
     sortByFirstName();
        return getContacts();
    }

    public List<Contact> getContactsSortedByLastName() {
        sortByLastName();
        return getContacts();
    }

    public List<Contact> getContactsSortedByCity() {
        sortByCity();
        return getContacts();
    }

    public boolean updateContact(Contact originalContact, Contact updatedContact, List<Group> selectedGroups) {
        // Check if the update would create a duplicate (if name is changed)
        if (!originalContact.getFirstName().equals(updatedContact.getFirstName()) || 
            !originalContact.getLastName().equals(updatedContact.getLastName())) {
            
            // Check for duplicate name
            for (Contact existingContact :  getContacts()) {
                // Skip comparing with the original contact
                if (existingContact.equals(originalContact)) continue;
                
                if (existingContact.getFirstName().equals(updatedContact.getFirstName()) && 
                    existingContact.getLastName().equals(updatedContact.getLastName())) {
                    return false; // Would create a duplicate name
                }
            }
        }
        
        // Check for duplicate phone numbers
        for (PhoneNumber newPhone : updatedContact.getPhoneNumbers()) {
            for (Contact existingContact :  getContacts()) {
                // Skip comparing with the original contact
                if (existingContact.equals(originalContact)) continue;
                
                for (PhoneNumber existingPhone : existingContact.getPhoneNumbers()) {
                    if (newPhone.getRegionCode() == existingPhone.getRegionCode() && 
                        newPhone.getNumber() == existingPhone.getNumber()) {
                        return false; // Would create a duplicate phone number
                    }
                }
            }
        }
        
        // First find and update the contact in all groups using the original values
        for (Group group :  getGroups()) {
            List<Contact> groupContacts = group.getContacts();
            for (int i = 0; i < groupContacts.size(); i++) {
                Contact groupContact = groupContacts.get(i);
                // Check if this is the contact we're updating using original values
                if (groupContact.getFirstName().equals(originalContact.getFirstName()) &&
                    groupContact.getLastName().equals(originalContact.getLastName()) &&
                    groupContact.getCity().equals(originalContact.getCity())) {
                    
                    // Update all fields with the new values
                    groupContact.setFirstName(updatedContact.getFirstName());
                    groupContact.setLastName(updatedContact.getLastName());
                    groupContact.setCity(updatedContact.getCity());
                    groupContact.setPhoneNumbers(updatedContact.getPhoneNumbers());
                }
            }
        }
        
        // Also update the contact in the main contact list
        for (Contact contact :  getContacts()) {
            if (contact.getFirstName().equals(originalContact.getFirstName()) &&
                contact.getLastName().equals(originalContact.getLastName()) &&
                contact.getCity().equals(originalContact.getCity())) {

                contact.setFirstName(updatedContact.getFirstName());
                contact.setLastName(updatedContact.getLastName());
                contact.setCity(updatedContact.getCity());
                contact.setPhoneNumbers(updatedContact.getPhoneNumbers());
            }
        }

        // --- Update group memberships ---
        // Remove from all current groups
        for (Group group : getGroups()) {
            group.removeContact(originalContact);
        }

        // Add to selected groups
        for (Group group : selectedGroups) {
            group.addContact(originalContact);
        }
        
        FileManager.saveContacts(getContacts());
        FileManager.saveGroups(getGroups());
        
        setChanged();
        notifyObservers();

        return true;
    }

}
