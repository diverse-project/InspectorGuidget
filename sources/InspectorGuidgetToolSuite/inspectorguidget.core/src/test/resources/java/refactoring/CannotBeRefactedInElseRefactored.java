package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A implements ActionListener {
	JButton but;
	JButton but2;

	A() {
		but = new JButton();
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("coucou");
			}
		});
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==but) {
		}else if(e.getSource()==but2) {
			System.out.println("hello");
		}
	}
}
