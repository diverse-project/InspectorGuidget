package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class E implements ActionListener {
	JButton but1;
	JButton but2;

	E() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);

		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Object bar = e.getSource();
		String cmd = e.getActionCommand();

		switch(cmd) {
			case "FOO":
				System.out.println(bar + " FOO");
				break;
			case "BAR":
				System.out.println(bar + " BAR");
				break;
		}
	}
}
