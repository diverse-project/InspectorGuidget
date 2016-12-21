package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A implements ActionListener {
	JButton but;

	A() {
		but = new JButton();
		but.addActionListener(this);
	}

	private void killA() {
		but.removeActionListener(this);
		but = null;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		System.out.println("coucou");
	}
}
