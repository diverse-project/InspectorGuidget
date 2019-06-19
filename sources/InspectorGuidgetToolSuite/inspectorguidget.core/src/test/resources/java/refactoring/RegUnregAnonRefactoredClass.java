package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A {
	JButton but;

	A() {
		but = new JButton();
		but.addActionListener(butCmd);
	}

	private void killA() {
		but.removeActionListener(butCmd);
		but = null;
	}
	private final ActionListener butCmd = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			System.out.println("coucou");
		}
	};
}
