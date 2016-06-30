import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class Fooooooooo {
	final MenusListener listener;

	Fooooooooo() {
		listener = new MenusListener();
	}

	public MenusListener getMenusListener() {
		return listener;
	}
}


class Toolbar extends JToolBar{
	Fooooooooo f;
	JMenuItem menuUpdate;

	Toolbar(Fooooooooo foo) {
		f = foo;
		MenusListener listener = foo.getMenusListener();

		JMenu helpMenu = new JMenu();

		menuUpdate = new JMenuItem();
		menuUpdate.addActionListener(listener);
		menuUpdate.setActionCommand(MenusListener.LABEL_CHECK_UPDATE);

		helpMenu.add(menuUpdate);

		JMenuItem menu = new JMenuItem();
		menu.addActionListener(listener);
		menu.setActionCommand(Resources.LABEL_SAVE);
		helpMenu.add(menu);

		menu = new JMenuItem();
		menu.addActionListener(listener);
		menu.setActionCommand(Resources.LABEL_SAVEAS);
		helpMenu.add(menu);


		add(helpMenu);
	}
}


class MenusListener implements ActionListener {
	public static final String LABEL_CHECK_UPDATE = "checkupdate";

	public MenusListener() {
		super();
	}


	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(src instanceof AbstractButton) {
			AbstractButton ab = ((AbstractButton) src);
			String label = ab.getActionCommand();

			if(label.equals(Resources.LABEL_SAVE)) {
				System.out.println("SAVVVVEe");
				return;
			}

			if(label.equals(Resources.LABEL_SAVEAS)) {
				System.out.println("SAVVVVEeASSSs");
				return;
			}

			if(label.equals(LABEL_CHECK_UPDATE)) {
				System.out.println("UUUUPDATTTTEe");
				return;
			}
		}
	}
}


class Resources {
	public final static String LABEL_SAVE = "SAVVVEEE";
	public final static String LABEL_SAVEAS = "SAVVVEEEAS";
}