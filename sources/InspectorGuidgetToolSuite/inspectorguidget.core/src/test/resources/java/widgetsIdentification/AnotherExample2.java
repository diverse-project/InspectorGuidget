import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MenusListener implements ActionListener {
	public static final String ACTION_CMD_SHORTCUTS 		= "shortcut";//$NON-NLS-1$

	public static final String ACTION_VISIBLE_ALL_FIGURES 	= "visibleAll";//$NON-NLS-1$


	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(src instanceof AbstractButton) {
			AbstractButton ab = ((AbstractButton) src);
			String label = ab.getActionCommand();
			boolean ok = ab.isSelected();

			if(label.equals(LaTeXDrawResources.LABEL_SAVE)) {
				System.out.println("mainFrame.save(false)");
				return;
			}

//			if(label.equals(LaTeXDrawResources.LABEL_SAVE_AS)) {
//				System.out.println("mainFrame.save(true)");
//				return;
//			}
//
//			if(label.equals(ACTION_VISIBLE_ALL_FIGURES)) {
//				System.out.println("drawPanel.getDraw().recenterFigures();");
//				return;
//			}
//
//			if(label.equals(ACTION_CMD_SHORTCUTS)) {
//				System.out.println("mainFrame.showShortcutsFrame();");
//				return;
//			}
//
//			if(label.equals(LaTeXDrawResources.LABEL_COPY)) {
//				System.out.println("drawPanel.copy();");
//				return;
//			}
		}
	}
}


class LaTeXDrawResources {
	public static final String LABEL_SAVE_AS = "LABEL_SAVE_AS";
	public static final String LABEL_COPY = "LABEL_COPY";
	public static final String LABEL_SAVE = "LABEL_SAVE";
}

class Toobar {
	Toobar(MenusListener ml) {
		JButton button;

		button = new JButton();
		button.addActionListener(ml);
		button.setActionCommand(LaTeXDrawResources.LABEL_SAVE);
	}
}
//class Menu {
//	Menu(MenusListener ml) {
//		JMenuItem menu;
//
//		menu = new JMenuItem(LaTeXDrawResources.LABEL_SAVE);
//		menu.addActionListener(ml);
//		menu.setActionCommand(LaTeXDrawResources.LABEL_SAVE);
//	}
//}