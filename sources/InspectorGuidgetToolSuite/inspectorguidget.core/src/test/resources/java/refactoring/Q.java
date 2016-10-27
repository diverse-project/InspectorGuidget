package java.refactoring;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Q implements ActionListener {
	Q() {
		JMenuItem menuItem = new JMenuItem();
		menuItem.setActionCommand("FOO");
		menuItem.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals("FOO")) {
			System.out.println("foo");
			return;
		}
	}
}
