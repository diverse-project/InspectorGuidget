package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JPanel;

class A extends JDialog {
	B b;

	A() {
		b = new B();
		b.addAdditionActionListener((ActionEvent e) -> System.out.println("coucou"));
		b.addDefaultActionListener(new DefaultListener());
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
