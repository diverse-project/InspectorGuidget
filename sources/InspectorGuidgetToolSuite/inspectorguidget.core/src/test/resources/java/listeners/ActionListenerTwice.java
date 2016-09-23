package foo;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.*;

class Foo implements ActionListener {
	public void actionPerformed(ActionEvent e) {

		JFileChooser chooser = new JFileChooser();

		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(
					JFileChooser.APPROVE_SELECTION)) {
					System.out.println("coucou1");
				}
			}
		});

		chooser.showOpenDialog(null);
	}
}
