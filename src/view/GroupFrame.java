package view;

import controller.ContactController;        
import controller.GroupController;
import model.Contact;
import model.ContactManager;
import model.Group;


import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GroupFrame extends JFrame implements Observer {

    private ContactManager contactManager;
     
    private DefaultListModel<Group> groupListModel;
    private JList<Group> groupJList;
    private DefaultListModel<Contact> contactListModel;
    private JList<Contact> contactJList;
    private JPanel buttonPanel;

    public GroupFrame(GroupController groupController, ContactManager contactManager) {
      //  this.groupController = groupController;
        this.contactManager = contactManager;

        // Register this GroupFrame as Observer to ContactManager
        this.contactManager.addObserver(this);
       // this.groupController.getManager().addObserver(this);

        setTitle("Groups");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
        setIconImage(icon.getImage());

        // Left panel for groups
        groupListModel = new DefaultListModel<>();
        groupJList = new JList<>(groupListModel);
        JScrollPane groupScroll = new JScrollPane(groupJList);
        groupScroll.setBorder(BorderFactory.createTitledBorder("Groups"));

        // Right panel for contacts in selected group
        contactListModel = new DefaultListModel<>();
        contactJList = new JList<>(contactListModel);
        JScrollPane contactScroll = new JScrollPane(contactJList);
        contactScroll.setBorder(BorderFactory.createTitledBorder("Contacts"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, groupScroll, contactScroll);
        splitPane.setDividerLocation(100);
        add(splitPane, BorderLayout.CENTER);

        // Bottom panel
        buttonPanel = new JPanel(new BorderLayout());
        JPanel centerButtons = new JPanel();
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // right-aligned return button

        JButton addGroupBtn = new JButton("Add new Group");
        JButton updateGroupBtn = new JButton("Update Group");
        JButton deleteGroupBtn = new JButton("Delete Group");

        // Match sizes for all buttons
        JButton[] allButtons = { addGroupBtn, updateGroupBtn, deleteGroupBtn };
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < allButtons.length; i++) {
            Dimension size = allButtons[i].getPreferredSize();
            if (size.width > maxWidth) maxWidth = size.width;
            if (size.height > maxHeight) maxHeight = size.height;
        }
        int paddedWidth = maxWidth + 10;
        Dimension buttonSize = new Dimension(paddedWidth, maxHeight);

        for (int i = 0; i < allButtons.length; i++) {
            allButtons[i].setPreferredSize(buttonSize);
            allButtons[i].setMaximumSize(buttonSize);
        }

        centerButtons.add(addGroupBtn);
        centerButtons.add(updateGroupBtn);
        centerButtons.add(deleteGroupBtn);

        buttonPanel.add(centerButtons, BorderLayout.CENTER);
        buttonPanel.add(rightPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load groups initially
        refreshGroupList();

        // Listeners
        groupJList.addListSelectionListener(e -> {
            Group selectedGroup = groupJList.getSelectedValue();
            if (selectedGroup != null) {
                refreshContactList(selectedGroup.getContacts());
            } else {
                contactListModel.clear();
            }
        });

        contactJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Contact selected = contactJList.getSelectedValue();
                    if (selected != null) {
                        new ViewContactFrame(selected, contactManager);
                    }
                }
            }
        });

        addGroupBtn.addActionListener(e -> new NewGroupFrame(groupController, this,contactManager));
        updateGroupBtn.addActionListener(e -> {
            Group selected = groupJList.getSelectedValue();
            if (selected != null) {
                new UpdateGroupFrame(contactManager, groupController, selected, this);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a group to update.");
            }
        });
        deleteGroupBtn.addActionListener(e -> {
            Group selected = groupJList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this group?");
                if (confirm == JOptionPane.YES_OPTION) {
                    groupController.removeGroup(selected);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a group to delete.");
            }
        });

        setVisible(true);
    }

    // Refresh group list UI from controller/model
    public void refreshGroupList() {
        groupListModel.clear();
        List<Group> groups = contactManager.getGroups();
        for (int i = 0; i < groups.size(); i++) {
            groupListModel.addElement(groups.get(i));
        }
    }

    // Refresh contact list UI for a group
    public void refreshContactList(List<Contact> contacts) {
        contactListModel.clear();
        for (int i = 0; i < contacts.size(); i++) {
            contactListModel.addElement(contacts.get(i));
        }
    }

    // Observer update method to react to ContactManagerEvents
    @Override
    public void update(Observable o, Object arg) {
       
                    Group selectedGroup = groupJList.getSelectedValue();
                    refreshGroupList();
                    if (selectedGroup != null) {
                        groupJList.setSelectedValue(selectedGroup, true);
                        refreshContactList(selectedGroup.getContacts());
                    } else {
                        contactListModel.clear();
                    }
               
           
    }
}
