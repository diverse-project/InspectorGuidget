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
		but2 = new JButton();
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==but) {
			System.out.println("coucou");
		}else {
			System.out.println("hello");
		}
	}
}
