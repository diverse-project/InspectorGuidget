package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class A {
	B but;

	A() {
		but = new B();
		but.addListener((ActionEvent e) -> System.out.println("coucou"));
	}

	class B extends JButton {
		public void addListener(ActionListener e) {

		}
	}
}


