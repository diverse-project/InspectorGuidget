package foo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class AA implements ActionListener {
	protected final static String A = "AAA";

	public AA() {
		JButton a = createButton();
		a.setActionCommand(A);
		a.addActionListener(this);
	}

	JButton createButton() {
		JButton b = new JButton();
		return b;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals(A)) {
			System.out.println("coucou");
		}
	}
}