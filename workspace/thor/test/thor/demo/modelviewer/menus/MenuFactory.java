package thor.demo.modelviewer.menus;

import javax.swing.JMenu;

public class MenuFactory {
	
	public enum MenuType {
		File,
		View,
		Help
	}
	
	public static JMenu createMenu(MenuType type) {
		if(type == MenuType.File) {
			return new MenuFile();
		} else if(type == MenuType.View) {
			return new MenuView();
		} else if(type == MenuType.Help) {
			return new MenuHelp();
		}
			
		return null;
	}
}