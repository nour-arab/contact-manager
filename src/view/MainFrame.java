package view;

import controller.ContactController;
import controller.GroupController;
import model.ContactManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame  {
	private ContactManager contactManager;
	private GroupController groupController;

	private JPanel mainPanel;
	private JLabel titleLabel;
	private JButton contactsBtn;
	private JButton groupsBtn;

	public MainFrame(ContactManager contactManager, GroupController groupController) {
		this.contactManager = contactManager;
		this.groupController = groupController;

		setTitle("Contact Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(null);

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Window Icon
		ImageIcon mainIcon = new ImageIcon(getClass().getResource("/resources/main.jpeg"));
		setIconImage(mainIcon.getImage());

		// Button Icons
		ImageIcon contactIcon = new ImageIcon(getClass().getResource("/resources/contacts.jpeg"));
		ImageIcon groupIcon = new ImageIcon(getClass().getResource("/resources/groups.jpeg"));
	

		contactIcon = new ImageIcon(contactIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		groupIcon = new ImageIcon(groupIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

		titleLabel = new JLabel("Contact Manager", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

		contactsBtn = new JButton("Contacts", contactIcon);
		setupButton(contactsBtn);
		contactsBtn.addActionListener(e -> {
			new ContactFrame(contactManager, groupController);
		});

		groupsBtn = new JButton("Groups", groupIcon);
		setupButton(groupsBtn);
		groupsBtn.addActionListener(e -> {
			new GroupFrame(groupController, contactManager);
			
		});

		
		mainPanel.add(titleLabel);
		mainPanel.add(contactsBtn);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		mainPanel.add(groupsBtn);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

		add(mainPanel);

	
		setVisible(true);
	}

	private void setupButton(JButton button) {
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setPreferredSize(new Dimension(200, 40));
		button.setMaximumSize(new Dimension(200, 40));
		button.setHorizontalTextPosition(SwingConstants.RIGHT);
		button.setFocusPainted(false);
		button.setOpaque(true);
		button.setBorderPainted(true);
		button.setContentAreaFilled(true);

	
	}

}
