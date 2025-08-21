package view;

import model.Contact;
import model.ContactManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ViewContactFrame extends JFrame implements Observer {

	private ContactManager contactManager;
	private Contact currentContact;

	private JLabel firstNameLabel;
	private JLabel lastNameLabel;
	private JLabel cityLabel;

	private DefaultListModel<String> phoneListModel;
	private JList<String> phoneJList;

	private DefaultListModel<String> groupListModel;
	private JList<String> groupJList;

	private JButton closeButton;

	public ViewContactFrame(Contact contact, ContactManager contactManager) {
		this.currentContact = contact;
		this.contactManager = contactManager;

		// Register as observer to contact manager
		this.contactManager.addObserver(this);

		setTitle("View Contact");
		setSize(400, 450);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setResizable(false);
		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
		setIconImage(icon.getImage());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		firstNameLabel = new JLabel();
		lastNameLabel = new JLabel();
		cityLabel = new JLabel();

		infoPanel.add(firstNameLabel);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		infoPanel.add(lastNameLabel);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		infoPanel.add(cityLabel);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));

		infoPanel.add(new JLabel("Phone Numbers:"));
		phoneListModel = new DefaultListModel<>();
		phoneJList = new JList<>(phoneListModel);
		phoneJList.setEnabled(false);
		JScrollPane phoneScroll = new JScrollPane(phoneJList);
		phoneScroll.setPreferredSize(new Dimension(350, 100));
		infoPanel.add(phoneScroll);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));

		infoPanel.add(new JLabel("Groups:"));
		groupListModel = new DefaultListModel<>();
		groupJList = new JList<>(groupListModel);
		groupJList.setEnabled(false);
		JScrollPane groupScroll = new JScrollPane(groupJList);
		groupScroll.setPreferredSize(new Dimension(350, 100));
		infoPanel.add(groupScroll);

		add(infoPanel, BorderLayout.CENTER);

		closeButton = new JButton("Close");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(closeButton);
		add(buttonPanel, BorderLayout.SOUTH);

		closeButton.addActionListener(e -> dispose());

		// Fill initial data
		refreshContactDetails();
		refreshGroups();

		// Cleanup observers on close
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				contactManager.deleteObserver(ViewContactFrame.this);
				dispose();
			}
		});

		setVisible(true);
	}

	// Refresh contact details labels and phone list
	private void refreshContactDetails() {
		if (currentContact == null)
			return;

		firstNameLabel.setText("First Name: " + currentContact.getFirstName());
		lastNameLabel.setText("Last Name: " + currentContact.getLastName());
		cityLabel.setText("City: " + currentContact.getCity());

		phoneListModel.clear();
		if (currentContact.getPhoneNumbers() != null && !currentContact.getPhoneNumbers().isEmpty()) {
			for (int i = 0; i < currentContact.getPhoneNumbers().size(); i++) {
				phoneListModel.addElement(currentContact.getPhoneNumbers().get(i).toString());
			}
		} else {
			phoneListModel.addElement("(No phone numbers)");
		}
	}

	// Refresh group list (groups that contain this contact)
	private void refreshGroups() {
		groupListModel.clear();
		List<String> groupNames = contactManager.getGroupNamesContainingContact(currentContact);
		if (groupNames != null && !groupNames.isEmpty()) {
			for (int i = 0; i < groupNames.size(); i++) {
				groupListModel.addElement(groupNames.get(i));
			}
		} else {
			groupListModel.addElement("(Not a member of any group)");
		}
	}

	// Observer update: react to ContactManagerEvent changes
	@Override
	public void update(Observable o, Object arg) {

		refreshContactDetails();
		refreshGroups(); // groups might have changed membership

	}
}
