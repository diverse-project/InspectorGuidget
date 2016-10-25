package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class J implements ActionListener {
	JButton but1;
	JButton but2;

	private static final String CMD_F1 = "foo1";
	private static final String CMD_F2 = "foo2";

	J() {
		but1 = new JButton("foo1");
		but1.setActionCommand(CMD_F1);
		but1.addActionListener(this);

		but2 = new JButton("foo2");
		but2.setActionCommand(CMD_F2);
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals(CMD_F1)) {
			System.out.println("coucou1");
			return;
		}
		if(e.getActionCommand().equals(CMD_F2)) {
			System.out.println("coucou2");
			return;
		}
	}
}
