package thor.demo.modelviewer.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL2;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import thor.demo.modelviewer.global.Session;
import thor.graphics.Point3D;


class MenuView extends JMenu {

	private int _displayModeLine = GL2.GL_LINE;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MenuView() {
		super("View");

        JMenu displayModeMenu = new JMenu("Display Mode");
        this.add(menuShowInfoItem());
        this.addSeparator();
        
        ButtonGroup displayModeGroup = new ButtonGroup();
        displayModeMenu.add(menuLineItem(displayModeGroup));
        displayModeMenu.add(menuFillItem(displayModeGroup));
        this.add(displayModeMenu);
	}
	private JMenuItem menuShowInfoItem() {
		JMenuItem item = new JMenuItem("Show Info");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				
				try {
					Point3D barycenter = Session.getInstance().getModel().getBarycenter();
					
					JOptionPane.showMessageDialog(Session.getInstance().getMainFrame(),
							"Model name: " + Session.getInstance().getModel().getName() + System.lineSeparator() +
							"Vertex Count: " + Session.getInstance().getModel().countVertices() + System.lineSeparator() +
							"Faces Count: " + Session.getInstance().getModel().countFaces() + System.lineSeparator() +
							System.lineSeparator() +
							"Barycenter: (" + barycenter.getX() + ", " + barycenter.getY() + ", " + barycenter.getZ() + ")" + System.lineSeparator() +
							"Is mainfold: " + Session.getInstance().getModel().isManifold() + System.lineSeparator() +
							"Surface Area: " + Session.getInstance().getModel().getSurfaceArea(),
							"Model Information",
							JOptionPane.INFORMATION_MESSAGE);
				} catch(java.lang.NullPointerException exception) {
					JOptionPane.showMessageDialog(Session.getInstance().getMainFrame(),
							exception.getMessage(),
							"Error information not found",
							JOptionPane.ERROR_MESSAGE);
				}
			}
        });
		return item;
	}

	private JMenuItem menuLineItem(ButtonGroup group) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem("Line", true);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(_displayModeLine != GL2.GL_LINE)
					Session.getInstance().setGlDrawMode(GL2.GL_LINE);
				
				_displayModeLine = GL2.GL_LINE;
			}
        });
		group.add(item);
		return item;
	}
	private JMenuItem menuFillItem(ButtonGroup group) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem("Fill");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if(_displayModeLine != GL2.GL_FILL)
					Session.getInstance().setGlDrawMode(GL2.GL_FILL);
				
				_displayModeLine = GL2.GL_FILL;
			}
        });
		group.add(item);
		return item;
	}
}