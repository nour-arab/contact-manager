package main;

import controller.ContactController;  

import controller.GroupController;
import model.Contact;
import model.ContactManager;
import model.Group;
import utils.FileManager;
import view.MainFrame;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel (optional for a nicer UI)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Load persisted data
        List<Contact> contacts = FileManager.loadContacts();
        List<Group> groups = FileManager.loadGroups();

        // Initialize model
        ContactManager manager = new ContactManager(contacts, groups);

        // Initialize controllers
       // ContactController contactController = new ContactController(manager);
        GroupController groupController = new GroupController(manager);

        // Run GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainFrame(manager, groupController));
    }
}
