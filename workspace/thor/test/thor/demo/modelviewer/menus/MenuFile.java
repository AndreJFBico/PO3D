package thor.demo.modelviewer.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import thor.demo.modelviewer.global.Session;
import thor.model.io.ModelIO;

class MenuFile extends JMenu {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MenuFile() {
		super("File");
				
        this.add(menuOpenItem());
        this.addSeparator();
        this.add(menuExitItem());
	}
	
	private JMenuItem menuOpenItem() {
		JMenuItem item = new JMenuItem("Open File...");
        item.setMnemonic('O');
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(Session.getInstance().getBrowseDirectory());
				
				chooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() { return "3D Models (*.obj; *.off)"; }
					@Override
					public boolean accept(File f) {
						if (f.isDirectory()) {
					        return true;
					    }
						String extension = null;
				        String s = f.getName();
				        int i = s.lastIndexOf('.');
				        if (i > 0 &&  i < s.length() - 1)
				        	extension = s.substring(i+1).toLowerCase();
					    if (extension != null) {
					        if (extension.equals("obj") ||
					            extension.equals("off")) {
					                return true;
					        }
					    }
						return false;
					}
				});
				int value = chooser.showOpenDialog(Session.getInstance().getMainFrame());
				
				if(value == JFileChooser.APPROVE_OPTION) {
					try {
						Session.getInstance().setModel(ModelIO.read(chooser.getSelectedFile()));
						Session.getInstance().setBrowseDirectory(chooser.getSelectedFile().getParentFile());
					} catch (IOException exception) {
						JOptionPane.showMessageDialog(Session.getInstance().getMainFrame(),
								exception.getMessage(),
								"Error: IOException",
							    JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
        return item;
	}
	
	private JMenuItem menuExitItem() {
		JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
        return item;
	}
}

