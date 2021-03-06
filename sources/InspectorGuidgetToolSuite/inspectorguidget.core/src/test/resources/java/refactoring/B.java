package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class B implements ActionListener {
	JButton but1;
	JButton but2;

	B() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);

		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals("FOO")) {
			System.out.println("coucou1");
			return;
		}
		if(e.getActionCommand().equals("BAR")) {
			System.out.println("coucou2");
			return;
		}
	}
}
