package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A {
	private final ActionListener butCmd = (ActionEvent e) -> System.out.println("coucou");
	JButton but;

	A() {
		but = new JButton();
		but.addActionListener(butCmd);
	}

	private void killA() {
		but.removeActionListener(butCmd);
		but = null;
	}
}
