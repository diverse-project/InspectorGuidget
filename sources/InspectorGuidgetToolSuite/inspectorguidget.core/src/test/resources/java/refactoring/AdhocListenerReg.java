package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class A implements ActionListener {
	B but;

	A() {
		but = new B();
		but.setActionCommand("FOO");
		but.addListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==but) {
			System.out.println("coucou");
		}
	}


	class B extends JButton {
		public void addListener(ActionListener e) {

		}
	}
}
