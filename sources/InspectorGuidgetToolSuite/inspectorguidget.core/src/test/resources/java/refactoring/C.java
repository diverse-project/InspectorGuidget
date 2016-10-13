package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class C implements ActionListener {
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

class A {
	JButton but1;
	JButton but2;

	A() {
		C c = new C();
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(c);

		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(c);
	}
}
