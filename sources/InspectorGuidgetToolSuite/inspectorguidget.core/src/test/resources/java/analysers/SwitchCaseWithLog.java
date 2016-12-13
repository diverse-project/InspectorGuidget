package foo;

import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

class Fooo implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
				case "a":
					System.out.println("coucou1");
					break;
				default:
					Logger.getAnonymousLogger().warning("foo" + cmd);
					break;
			}
	}
}
