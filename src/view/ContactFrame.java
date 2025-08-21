package view;

import controller.ContactController;     

import controller.GroupController;
import model.Contact;
import model.ContactManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ContactFrame extends JFrame implements Observer {
	private ContactManager contactManager;
	//private GroupController groupController;
	private DefaultListModel<Contact> listModel;
	private JList<Contact> contactJList;
	private JTextField searchField;

	// Panels and buttons to apply theme on
	private JPanel topPanel;
	private JPanel leftPanel;
	private JButton sortFirstNameBtn, sortLastNameBtn, sortCityBtn;
	private JButton addBtn, updateBtn, deleteBtn, viewBtn;
	
	
	public ContactFrame(ContactManager contactManager, GroupController groupController) {
		this.contactManager = contactManager;
		
		// Register this contactFrame as Observer to ContactManager
        this.contactManager.addObserver(this);
		
		setTitle("Contacts");
		setSize(800, 500);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
		setIconImage(icon.getImage());

		// Top Panel: Search and Sort
		topPanel = new JPanel(new FlowLayout());

		sortFirstNameBtn = new JButton("Sort by First name");
		sortLastNameBtn = new JButton("Sort by Last name");
		sortCityBtn = new JButton("Sort by City");

		topPanel.add(sortFirstNameBtn);
		topPanel.add(sortLastNameBtn);
		topPanel.add(sortCityBtn);

		searchField = new JTextField(20);
		topPanel.add(new JLabel("Search:"));
		topPanel.add(searchField);

		add(topPanel, BorderLayout.NORTH);

		// Center Panel: List of Contacts
		listModel = new DefaultListModel<>();
		contactJList = new JList<>(listModel);
		
		JScrollPane scrollPane = new JScrollPane(contactJList);
		add(scrollPane, BorderLayout.CENTER);

		// Left Panel with BorderLayout to split top/bottom
		leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		addBtn = new JButton("Add new Contact");
		updateBtn = new JButton("Update Contact");
		deleteBtn = new JButton("Delete Contact");
		viewBtn = new JButton("View Contact");

		// Group all buttons for equal width
		JButton[] buttons = { addBtn, updateBtn, deleteBtn, viewBtn  };
		int maxWidth = 0;
		for (int i = 0; i < buttons.length; i++) {
			maxWidth = Math.max(maxWidth, buttons[i].getPreferredSize().width);
		}
		for (int i = 0; i < buttons.length; i++) {
			Dimension size = buttons[i].getPreferredSize();
			size.width = maxWidth;
			buttons[i].setPreferredSize(size);
			buttons[i].setMaximumSize(size);
			buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
		}

		// Top panel for main buttons
		JPanel buttonGroupPanel = new JPanel();
		buttonGroupPanel.setLayout(new BoxLayout(buttonGroupPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < buttons.length; i++) {
			buttonGroupPanel.add(buttons[i]);
			buttonGroupPanel.add(Box.createVerticalStrut(10));
		}
		leftPanel.add(buttonGroupPanel, BorderLayout.NORTH);

		add(leftPanel, BorderLayout.WEST);

		// Listeners
		sortFirstNameBtn.addActionListener(e -> refreshList(contactManager.getContactsSortedByFirstName()));
		sortLastNameBtn.addActionListener(e -> refreshList(contactManager.getContactsSortedByLastName()));
		sortCityBtn.addActionListener(e -> refreshList(contactManager.getContactsSortedByCity()));

		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				search();
			}

			public void removeUpdate(DocumentEvent e) {
				search();
			}

			public void insertUpdate(DocumentEvent e) {
				search();
			}

			public void search() {
				String query = searchField.getText();
				List<Contact> results = contactManager.searchContacts(query);
				refreshList(results);
			}
		});

		addBtn.addActionListener(e -> new NewContactFrame(contactManager, this));

		viewBtn.addActionListener(e -> {
			Contact selected = contactJList.getSelectedValue();
			if (selected != null) {
				new ViewContactFrame(selected, contactManager);
			} else {
				JOptionPane.showMessageDialog(this, "Please select a contact to view.");
			}
		});

		updateBtn.addActionListener(e -> {
			Contact selected = contactJList.getSelectedValue();
			if (selected != null) {
				new UpdateContactFrame(contactManager, selected, this);
			} else {
				JOptionPane.showMessageDialog(this, "Please select a contact to update.");
			}
		});

		deleteBtn.addActionListener(e -> {
			Contact selected = contactJList.getSelectedValue();
			if (selected != null) {
				int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this contact?");
				if (confirm == JOptionPane.YES_OPTION) {
					contactManager.removeContact(selected);
					refreshList(contactManager.getContacts());
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please select a contact to delete.");
			}
		});


		refreshList(contactManager.getContacts());
		
		setVisible(true);
	}

	public void refreshList(List<Contact> contacts) {
		listModel.clear();
		for (int i = 0; i < contacts.size(); i++) {
			listModel.addElement(contacts.get(i));
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		refreshList(contactManager.getContacts());
		
	}

}