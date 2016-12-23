package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A implements ActionListener {
	JButton but;
	JButton but2;

	A() {
		but = new JButton();
		but.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==but) {
			System.out.println("coucou");
		}else if(e.getSource()==but2) {
			System.out.println("hello");
		}
	}
}
