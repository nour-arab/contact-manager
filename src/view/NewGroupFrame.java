package view;

import model.ContactManager;
import controller.GroupController;
import model.Contact;
import model.Group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

public class NewGroupFrame extends JFrame implements Observer {

	private JTextField nameField;
	private JTextArea descriptionArea;
	private JList<Contact> contactJList;
	private GroupController groupController;
	private GroupFrame parentFrame;
	private ContactManager contactManager;

	// Panels and buttons
	private JPanel formPanel;
	private JPanel buttonPanel;
	private JButton saveBtn;
	private JButton cancelBtn;
	private JScrollPane contactScroll;

	public NewGroupFrame(GroupController controller, GroupFrame parent , ContactManager contactManager) {
		this.groupController = controller;
		this.parentFrame = parent;
		this.contactManager = contactManager;

		contactManager.addObserver(this);
		setTitle("New Group");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
		setIconImage(icon.getImage());

		// Form Panel
		formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		nameField = new JTextField();
		descriptionArea = new JTextArea(3, 20);
		formPanel.add(new JLabel("Group Name:"));
		formPanel.add(nameField);
		formPanel.add(new JLabel("Description:"));
		formPanel.add(new JScrollPane(descriptionArea));

		add(formPanel, BorderLayout.NORTH);

		// Contact list
		contactJList = new JList<>();
		contactJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		contactScroll = new JScrollPane(contactJList);
		contactScroll.setBorder(BorderFactory.createTitledBorder("Add Contacts to Group"));

		add(contactScroll, BorderLayout.CENTER);

		// Button panel
		buttonPanel = new JPanel();
		saveBtn = new JButton("Save Group");
		cancelBtn = new JButton("Cancel");
		buttonPanel.add(saveBtn);
		buttonPanel.add(cancelBtn);
		add(buttonPanel, BorderLayout.SOUTH);

		loadContacts();

		setVisible(true);

		// --- Listeners ---
		saveBtn.addActionListener((ActionEvent e) -> {
			String name = nameField.getText().trim();
			String description = descriptionArea.getText().trim();
			List<Contact> selectedContacts = contactJList.getSelectedValuesList();

			if (name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Group name is required.");
				return;
			}

			Group newGroup = new Group(name, description);
			for (int i = 0; i < selectedContacts.size(); i++) {
				newGroup.addContact(selectedContacts.get(i));
			}

			groupController.addGroup(newGroup);
			parentFrame.refreshGroupList();
			dispose();
		});

		cancelBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?");
			if (confirm == JOptionPane.YES_OPTION) {
				dispose();
			}
		});

		// Unregister from observable on window close if needed (optional)
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}

	private void loadContacts() {
		List<Contact> allContacts = groupController.getAllContacts();
		DefaultListModel<Contact> model = new DefaultListModel<>();
		for (Contact contact : allContacts) {
			model.addElement(contact);
		}
		contactJList.setModel(model);
	}

	// Observer update method (called when the observable notifies observers)
	@Override
	public void update(Observable o, Object arg) {
		loadContacts();
	}
}
