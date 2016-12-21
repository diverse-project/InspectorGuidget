package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class A implements ActionListener {
	JButton but;

	A() {
		if(but!=null) {
			but.addActionListener(this);
		}
	}

	private void killA() {
		if(but!=null) {
			but.removeActionListener(this);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		System.out.println("coucou");
	}
}
