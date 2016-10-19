package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class F implements ActionListener {
	JButton b1 = new JButton();
	JButton b2 = new JButton();

	public F() {
		b1 = new JButton("foo");
		b1.addActionListener(this);
		b2 = new JButton("bar");
		b2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==b1) {
			dispatch1();
			return;
		}
		if(e.getSource()==b2) {
			dispatch2();
			return;
		}
	}

	private void dispatch1() {
		System.out.println("dispatch foo");
	}

	private void dispatch2() {
		System.out.println("dispatch bar");
	}
}
