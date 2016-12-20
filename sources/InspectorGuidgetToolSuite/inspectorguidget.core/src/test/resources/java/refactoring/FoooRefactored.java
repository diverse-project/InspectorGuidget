package foo;

import java.awt.event.ActionEvent;
import javax.swing.JButton;

class AA {
	public AA() {
		JButton a = createButton();
		a.addActionListener((ActionEvent e) -> System.out.println("coucou"));
	}

	JButton createButton() {
		JButton b = new JButton();
		return b;
	}
}