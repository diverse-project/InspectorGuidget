package java.refactoring;

import javax.swing.*;
import java.awt.event.*;

class D implements ActionListener {
	JButton but1;

	D() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Object bar = e.getSource();
		String cmd = e.getActionCommand();

		if(cmd.equals("FOO")) {
			System.out.println(bar);
			return;
		}
	}
}
