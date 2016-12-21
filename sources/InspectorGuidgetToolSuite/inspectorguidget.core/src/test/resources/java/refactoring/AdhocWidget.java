package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class A implements ActionListener {
	MyB but;

	A() {
		but = new MyB("foo", "FOO", this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==but) {
			System.out.println("coucou");
		}
	}

	private static class MyB extends JButton {
		public MyB(String text, String command, ActionListener listener) {
			super(text);
			setActionCommand(command);
			addActionListener(listener);
		}
	}
}
