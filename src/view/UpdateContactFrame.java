package view;

import controller.ContactController; 
import model.Contact;
import model.ContactManager;
import model.Group;

import model.PhoneNumber;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UpdateContactFrame extends JFrame implements Observer {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField cityField;
    private DefaultListModel<String> phoneListModel;
    private JList<String> phoneList;
    private JTextField regionCodeField;
    private JTextField localNumberField;
    private JList<String> groupList;
    private DefaultListModel<String> groupListModel;

    private JPanel formPanel;
    private JPanel phoneBtnPanel;
    private JPanel buttonPanel;
    private JButton addPhoneBtn;
    private JButton removePhoneBtn;
    private JButton saveBtn;
    private JButton cancelBtn;
    private JScrollPane phoneScroll;
    private JScrollPane groupScroll;

    private String originalFirstName;
    private String originalLastName;
    private String originalCity;
    private List<PhoneNumber> originalPhoneNumbers;

    private ContactManager contactManager;
    private Contact contact;
    private ContactFrame parent;

    public UpdateContactFrame(ContactManager contactManager, Contact contact, ContactFrame parent) {
        this.contactManager = contactManager;
        this.contact = contact;
        this.parent = parent;

        setTitle("Update Contact");
        setSize(400, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
        setIconImage(icon.getImage());

        originalFirstName = contact.getFirstName();
        originalLastName = contact.getLastName();
        originalCity = contact.getCity();
        originalPhoneNumbers = new ArrayList<>(contact.getPhoneNumbers());

        formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        firstNameField = new JTextField(contact.getFirstName());
        lastNameField = new JTextField(contact.getLastName());
        cityField = new JTextField(contact.getCity());

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);
        formPanel.add(new JLabel("Phone Numbers:"));

        phoneListModel = new DefaultListModel<>();
        List<PhoneNumber> numbers = contact.getPhoneNumbers();
        for (int i = 0; i < numbers.size(); i++) {
            PhoneNumber pn = numbers.get(i);
            phoneListModel.addElement("+" + pn.getRegionCode() + " " + pn.getNumber());
        }
     // Register this updatecontactFrame as Observer to ContactManager
        this.contactManager.addObserver(this);

        phoneList = new JList<>(phoneListModel);
        phoneScroll = new JScrollPane(phoneList);
        formPanel.add(phoneScroll);

        formPanel.add(new JLabel("Phone Number:"));

        JPanel phoneInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        phoneInputPanel.setOpaque(false);

        phoneInputPanel.add(new JLabel("Region:"));
        regionCodeField = new JTextField();
        regionCodeField.setColumns(4);
        phoneInputPanel.add(regionCodeField);

        phoneInputPanel.add(new JLabel("Number:"));
        localNumberField = new JTextField();
        localNumberField.setColumns(10);
        phoneInputPanel.add(localNumberField);

        formPanel.add(phoneInputPanel);

        addPhoneBtn = new JButton("Add Phone");
        removePhoneBtn = new JButton("Remove Selected");

        phoneBtnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        phoneBtnPanel.add(addPhoneBtn);
        phoneBtnPanel.add(removePhoneBtn);
        formPanel.add(phoneBtnPanel);

        // Group list section
        formPanel.add(new JLabel("Groups:"));
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupScroll = new JScrollPane(groupList);
        formPanel.add(groupScroll);
        refreshGroupList();

        add(formPanel, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Listeners ---
        addPhoneBtn.addActionListener(e -> {
            String region = regionCodeField.getText().trim();
            String local = localNumberField.getText().trim();

            if (!region.matches("\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Region code must be exactly 2 digits.");
                return;
            }

            if (!local.matches("\\d{6,8}")) {
                JOptionPane.showMessageDialog(this, "Local number must be between 6 and 8 digits.");
                return;
            }

            String display = "+" + region + " " + local;
            if (!phoneListModel.contains(display)) {
                phoneListModel.addElement(display);
                regionCodeField.setText("");
                localNumberField.setText("");
            }
        });

        removePhoneBtn.addActionListener(e -> {
            String selected = phoneList.getSelectedValue();
            if (selected != null) {
                phoneListModel.removeElement(selected);
            }
        });

        saveBtn.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First and Last name are required.");
                return;
            }

            if (phoneListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "At least one phone number is required.");
                return;
            }

            Contact updatedContact = new Contact(firstName, lastName, cityField.getText().trim(), new ArrayList<>());

            for (int i = 0; i < phoneListModel.size(); i++) {
                String entry = phoneListModel.getElementAt(i);
                try {
                    String[] parts = entry.split(" ");
                    String regionStr = parts[0].startsWith("+") ? parts[0].substring(1) : parts[0];
                    int region = Integer.parseInt(regionStr);
                    int local = Integer.parseInt(parts[1]);
                    updatedContact.getPhoneNumbers().add(new PhoneNumber(region, local));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid phone entry: " + entry);
                    return;
                }
            }

            // Build selected group list
            List<Group> selectedGroups = new ArrayList<>();
            List<Group> allGroups = contactManager.getGroups();
            for (int i = 0; i < groupListModel.size(); i++) {
                if (groupList.isSelectedIndex(i)) {
                    String name = groupListModel.get(i);
                    for (int j = 0; j < allGroups.size(); j++) {
                        Group g = allGroups.get(j);
                        if (g.getName().equals(name)) {
                            selectedGroups.add(g);
                            break;
                        }
                    }
                }
            }

            boolean updated = contactManager.updateContact(contact, updatedContact, selectedGroups);
            if (!updated) {
                JOptionPane.showMessageDialog(this,
                        "Update failed: would create a duplicate contact name or phone number.",
                        "Duplicate Contact", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Removed: controller.getManager().notifyContactUpdated(updatedContact);
            parent.refreshList(contactManager.getContactsSortedByFirstName());
            JOptionPane.showMessageDialog(this, "Contact updated successfully.");
            dispose();
        });

        cancelBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Discard changes?", "Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        // Register this frame as an observer for ContactManager events
        contactManager.addObserver(this);

        setVisible(true);
    }

    private void refreshGroupList() {
        groupListModel.clear();
        List<Group> groups = contactManager.getGroups();
        for (int i = 0; i < groups.size(); i++) {
            groupListModel.addElement(groups.get(i).getName());
        }

        // Pre-select the groups this contact belongs to
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            if (g.getContacts().contains(contact)) {
                selectedIndices.add(i);
            }
        }
        int[] indices = new int[selectedIndices.size()];
        for (int i = 0; i < selectedIndices.size(); i++) {
            indices[i] = selectedIndices.get(i);
        }
        groupList.setSelectedIndices(indices);
    }

    @Override
    public void update(Observable o, Object arg) {
       
                // Refresh group list on relevant group changes
            	refreshGroupList();
            
    }
}
