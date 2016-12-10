package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class A implements ListSelectionListener, ActionListener {
	B b;

	A() {
		b = new B();
		b.addListSelectionListener(this);
		b.addListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			System.out.println("coucou");
		}
	}

	public void valueChanged(ListSelectionEvent e) {

	}

	class B extends JPanel implements ListSelectionListener {
		public void addListener(ActionListener e) {

		}

		public void valueChanged(ListSelectionEvent e) {

		}

		public void addListSelectionListener(ListSelectionListener l) {

		}
	}
}
