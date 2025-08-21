package utils;

import model.Contact;      
import model.Group;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String CONTACTS_FILE = "contacts.dat";
    private static final String GROUPS_FILE = "groups.dat";

    // Save contacts to file
    public static void saveContacts(List<Contact> contacts) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CONTACTS_FILE))) {
            out.writeObject(contacts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load contacts from file
    @SuppressWarnings("unchecked")
    public static List<Contact> loadContacts() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CONTACTS_FILE))) {
            return (List<Contact>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();  // return empty list if file doesn't exist or error occurs
        }
    }
    
    
    // Save groups to file
    public static void saveGroups(List<Group> groups) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(GROUPS_FILE))) {
            out.writeObject(groups);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load groups from file
    @SuppressWarnings("unchecked")
    public static List<Group> loadGroups() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(GROUPS_FILE))) {
            return (List<Group>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
