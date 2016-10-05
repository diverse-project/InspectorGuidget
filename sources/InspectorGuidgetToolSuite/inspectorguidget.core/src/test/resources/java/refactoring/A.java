package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A implements ActionListener {
	JButton but;

	A() {
		but = new JButton("foo");
		but.setActionCommand("FOO");
		but.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		System.out.println("coucou");
	}
}
