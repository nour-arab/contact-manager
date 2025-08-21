package view;

import controller.GroupController;
import model.Contact;
import model.ContactManager;
import model.Group;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class UpdateGroupFrame extends JFrame implements Observer {
	private ContactManager contactManager;
	private JTextField nameField;
	private JTextArea descriptionArea;

	DefaultListModel<Contact> model;
	private JList<Contact> contactJList;
	private GroupController groupController;
	private Group group;
	private GroupFrame parentFrame;

	private JPanel formPanel;
	private JScrollPane descriptionScroll;
	private JScrollPane contactScroll;
	private JPanel buttonPanel;
	private JButton saveBtn;
	private JButton cancelBtn;

	private TitledBorder contactBorder;

	public UpdateGroupFrame(ContactManager contactManager, GroupController controller, Group groupToEdit,
			GroupFrame parent) {
		this.groupController = controller;
		this.group = groupToEdit;
		this.parentFrame = parent;
		this.contactManager = contactManager;

		setTitle("Update Group");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
		setIconImage(icon.getImage());

		// Register this GroupFrame as Observer to ContactManager
		this.contactManager.addObserver(this);

		// Form Panel
		formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		nameField = new JTextField(group.getName());
		descriptionArea = new JTextArea(group.getDescription(), 3, 20);
		descriptionScroll = new JScrollPane(descriptionArea);

		formPanel.add(new JLabel("Group Name:"));
		formPanel.add(nameField);
		formPanel.add(new JLabel("Description:"));
		formPanel.add(descriptionScroll);

		add(formPanel, BorderLayout.NORTH);

		// Contact list
		contactJList = new JList<>();
		contactJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		contactScroll = new JScrollPane(contactJList);

		contactBorder = BorderFactory.createTitledBorder("Contacts in Group");
		contactScroll.setBorder(contactBorder);

		add(contactScroll, BorderLayout.CENTER);

		// Bottom Buttons
		buttonPanel = new JPanel();
		saveBtn = new JButton("Save Changes");
		cancelBtn = new JButton("Cancel");
		buttonPanel.add(saveBtn);
		buttonPanel.add(cancelBtn);
		add(buttonPanel, BorderLayout.SOUTH);

		// Load all contacts
		List<Contact> allContacts = controller.getAllContacts();
		model = new DefaultListModel<>();
		for (int i = 0; i < allContacts.size(); i++) {
			model.addElement(allContacts.get(i));
		}
		contactJList.setModel(model);

		// Pre-select contacts already in the group
		int size = allContacts.size();
		int[] selectedIndices = new int[size];
		int count = 0;
		for (int i = 0; i < size; i++) {
			Contact c = allContacts.get(i);
			if (group.containsContact(c)) {
				selectedIndices[count++] = i;
			}
		}
		int[] trimmed = new int[count];
		for (int i = 0; i < count; i++) {
			trimmed[i] = selectedIndices[i];
		}
		contactJList.setSelectedIndices(trimmed);

		setVisible(true);

		// --- Button Listeners ---
		saveBtn.addActionListener((ActionEvent e) -> {
			String name = nameField.getText().trim();
			String description = descriptionArea.getText().trim();
			List<Contact> selectedContacts = contactJList.getSelectedValuesList();

			if (name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Group name is required.");
				return;
			}

			boolean detailsChanged = !group.getName().equals(name) || !group.getDescription().equals(description);
			boolean membershipChanged = true;

			group.setName(name);
			group.setDescription(description);
			group.clearContacts();
			for (int i = 0; i < selectedContacts.size(); i++) {
				group.addContact(selectedContacts.get(i));
			}

			groupController.updateGroup(group);

			if (detailsChanged) {
				groupController.getManager().groupDetailsChanged();
			}
			if (membershipChanged) { // Always notify for membership changes for now
				groupController.getManager().groupMembershipChanged();
			}

			dispose();
		});

		cancelBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?");
			if (confirm == JOptionPane.YES_OPTION) {
				dispose();
			}
		});

	}

	// Refresh contact list UI for a group
	public void refreshContactList(List<Contact> contacts) {
		model.clear();
		for (int i = 0; i < contacts.size(); i++) {
			model.addElement(contacts.get(i));
		}
		int size = contacts.size();
		int[] selectedIndices = new int[size];
		int count = 0;
		for (int i = 0; i < size; i++) {
			Contact c = contacts.get(i);
			if (group.containsContact(c)) {
				selectedIndices[count++] = i;
			}
		}
		int[] trimmed = new int[count];
		for (int i = 0; i < count; i++) {
			trimmed[i] = selectedIndices[i];
		}
		contactJList.setSelectedIndices(trimmed);
	}

	@Override
	public void update(Observable o, Object arg) {
		refreshContactList(contactManager.getContacts());

	}

}
