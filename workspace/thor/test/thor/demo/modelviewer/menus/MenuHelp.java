package thor.demo.modelviewer.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import thor.demo.modelviewer.global.Session;


class MenuHelp extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MenuHelp() {
		super("Help");
				
        this.add(menuHelpItem());
        this.add(menuAboutItem());
	}
	

	private JMenuItem menuHelpItem() {
		JMenuItem item = new JMenuItem("Help");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(Session.getInstance().getMainFrame(),
						"Sorry... the help you're looking for" + System.lineSeparator() +
						"is in another castle" + System.lineSeparator() +
						System.lineSeparator() +
						"If you have any problem, either ask in the forum" + System.lineSeparator() +
						"or send an email to: pmbp@ist.utl.pt" + System.lineSeparator(),
						"You're on your own...",
						JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon(MenuHelp.class.getResource("NoHelpIcon.png")));
			}
        });
		return item;
	}

	private JMenuItem menuAboutItem() {
		JMenuItem item = new JMenuItem("About");
        item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(Session.getInstance().getMainFrame(),
						"Model Viewer v1.0" + System.lineSeparator() +
						"Made by Pedro B. Pascoal" + System.lineSeparator() +
						"pmbp@ist.utl.pt" + System.lineSeparator() +
						"http://web.ist.utl.pt/pmbp/" + System.lineSeparator() +
						"(C) Copyright 2012-2013" + System.lineSeparator(),
						"About",
						JOptionPane.INFORMATION_MESSAGE);
			}
        });
		return item;
	}
}