package fr.inria.diverse.torgen.inspectorguidget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class G implements ActionListener {
	JButton b1 = new JButton();
	JButton b2 = new JButton();

	public G() {
		b1 = new JButton("foo");
		b1.addActionListener(this);
		b2 = new JButton("bar");
		b2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==b1) {
			System.out.println("b1");
		}else if(e.getSource()==b2) {
			System.out.println("b2");
		}
	}
}
