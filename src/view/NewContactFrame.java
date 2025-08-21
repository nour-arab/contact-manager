package view;

import controller.ContactController;
import model.Contact;
import model.ContactManager;
import model.Group;
import model.PhoneNumber;
import utils.FileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

public class NewContactFrame extends JFrame implements Observer {
	// Input fields
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField cityField;
	private JTextField regionCodeField;
	private JTextField localNumberField;

	// Phone numbers UI
	private DefaultListModel<String> phoneListModel;
	private JList<String> phoneList;

	// Buttons
	private JButton addPhoneBtn;
	private JButton saveBtn;
	private JButton cancelBtn;

	// Panels
	private JPanel formPanel;
	private JPanel buttonPanel;
	private JPanel groupPanel;

	// Controller and parent frame references
	// private ContactController contactController;
	private ContactManager contactManager;
	private ContactFrame parentFrame;

	// Data
	private List<PhoneNumber> phoneNumbers;
	private List<JCheckBox> groupCheckBoxes;

	public NewContactFrame(ContactManager contactManager, ContactFrame parent) {
		// this.contactController = controller;
		this.parentFrame = parent;
		this.contactManager = contactManager;
		this.phoneNumbers = new ArrayList<>();
		this.groupCheckBoxes = new ArrayList<>();
		contactManager.addObserver(this);
		setupFrame();
		buildForm();
		addListeners();

		setVisible(true);
	}

	// Setup main frame properties
	private void setupFrame() {
		setTitle("New Contact");
		setSize(400, 600);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
		setIconImage(icon.getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	// Build UI form components
	private void buildForm() {
		formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Input fields
		firstNameField = new JTextField();
		lastNameField = new JTextField();
		cityField = new JTextField();
		regionCodeField = new JTextField();
		localNumberField = new JTextField();

		phoneListModel = new DefaultListModel<>();
		phoneList = new JList<>(phoneListModel);
		JScrollPane phoneScroll = new JScrollPane(phoneList);

		// Add input fields and labels
		formPanel.add(new JLabel("First Name:"));
		formPanel.add(firstNameField);
		formPanel.add(new JLabel("Last Name:"));
		formPanel.add(lastNameField);
		formPanel.add(new JLabel("City:"));
		formPanel.add(cityField);

		// Phone input section
		formPanel.add(new JLabel("Phone Number:"));
		JPanel phoneInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		phoneInputPanel.setOpaque(false);
		phoneInputPanel.add(new JLabel("Region:"));
		regionCodeField.setColumns(4);
		phoneInputPanel.add(regionCodeField);
		phoneInputPanel.add(new JLabel("Number:"));
		localNumberField.setColumns(10);
		phoneInputPanel.add(localNumberField);

		addPhoneBtn = new JButton("Add Phone");

		formPanel.add(phoneInputPanel);
		formPanel.add(addPhoneBtn);
		formPanel.add(new JLabel("Phone List:"));
		formPanel.add(phoneScroll);

		// Groups checkboxes panel inside a scroll pane
		formPanel.add(new JLabel("Add to Groups:"));
		groupPanel = new JPanel();
		groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
		loadGroupsToCheckboxes();
		JScrollPane groupScrollPane = new JScrollPane(groupPanel);
		groupScrollPane.setPreferredSize(new Dimension(300, 150));
		groupScrollPane.setMaximumSize(new Dimension(300, 200));
		formPanel.add(groupScrollPane);

		// Buttons panel (Save, Cancel)
		buttonPanel = new JPanel(new FlowLayout());
		saveBtn = new JButton("Save");
		cancelBtn = new JButton("Cancel");
		buttonPanel.add(saveBtn);
		buttonPanel.add(cancelBtn);

		add(formPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	// Load groups from controller and create checkboxes
	private void loadGroupsToCheckboxes() {
		groupPanel.removeAll();
		groupCheckBoxes.clear();

		List<Group> groups = contactManager.getGroups();

		for (Group group : groups) {
			JCheckBox checkBox = new JCheckBox(group.getName());
			groupCheckBoxes.add(checkBox);
			groupPanel.add(checkBox);
		}

		groupPanel.revalidate();
		groupPanel.repaint();
	}

	// Add event listeners for buttons
	private void addListeners() {
		// Add Phone button
		addPhoneBtn.addActionListener(e -> {
			String regionCodeText = regionCodeField.getText().trim();
			String localNumberText = localNumberField.getText().trim();

			if (!regionCodeText.matches("\\d{2}")) {
				JOptionPane.showMessageDialog(this, "Region code must be exactly 2 digits.");
				return;
			}

			if (!localNumberText.matches("\\d{6}")) {
				JOptionPane.showMessageDialog(this, "Local number must be 6 digits.");
				return;
			}

			int regionCode = Integer.parseInt(regionCodeText);
			int localNumber = Integer.parseInt(localNumberText);

			PhoneNumber phoneNumber = new PhoneNumber(regionCode, localNumber);
			phoneNumbers.add(phoneNumber);
			phoneListModel.addElement(phoneNumber.toString());

			regionCodeField.setText("");
			localNumberField.setText("");
		});

		// Save button
		saveBtn.addActionListener(e -> {
			String firstName = firstNameField.getText().trim();
			String lastName = lastNameField.getText().trim();
			String city = cityField.getText().trim();

			if (firstName.isEmpty() || lastName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "First and Last name are required.");
				return;
			}

			if (phoneNumbers.isEmpty()) {
				JOptionPane.showMessageDialog(this, "At least one phone number is required.");
				return;
			}

			Contact newContact = new Contact(firstName, lastName, city, phoneNumbers);

			// Add contact to selected groups
			List<Group> allGroups = contactManager.getGroups();

			for (int i = 0; i < groupCheckBoxes.size(); i++) {
				JCheckBox checkBox = groupCheckBoxes.get(i);
				if (checkBox.isSelected()) {
					String groupName = checkBox.getText();
					for (Group group : allGroups) {
						if (group.getName().equals(groupName)) {
							group.addContact(newContact);
							break;
						}
					}
				}
			}

			boolean added = contactManager.addContact(newContact);
			if (added) {
			}

			if (!added) {
				JOptionPane.showMessageDialog(this, "A contact with this name or phone number already exists.",
						"Duplicate Contact", JOptionPane.ERROR_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(this, "Contact saved successfully!");

			dispose();
		});

		// Cancel button
		cancelBtn.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?");
			if (confirm == JOptionPane.YES_OPTION) {
				dispose();
			}
		});
	}

	// Observer update method called when observable notifies observers
	// loadGroupsToCheckboxes
	@Override
	public void update(Observable o, Object arg) {
		loadGroupsToCheckboxes();
	}
}
