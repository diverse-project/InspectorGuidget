package java.refactoring;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class A extends JDialog implements ActionListener {
	B b;

	A() {
		b = new B();
		b.addAdditionActionListener(this);
		b.addDefaultActionListener(new DefaultListener());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			System.out.println("coucou");
		}
	}

	private class DefaultListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	class B extends JPanel {
		public void addAdditionActionListener(ActionListener e) {

		}

		public void addDefaultActionListener(ActionListener l) {

		}
	}
}
