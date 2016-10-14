package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class D implements ActionListener {
	JButton but1;
	JButton but2;

	D() {
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

		if(cmd.equals("FOO")) {
			System.out.println(bar);
			return;
		}
		if(cmd.equals("BAR")) {
			System.out.println(bar);
			return;
		}
	}
}
