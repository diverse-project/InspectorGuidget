

package fr.inria.diverse.torgen.inspectorguidget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class G {
	JButton b1 = new JButton();

	JButton b2 = new JButton();

	public G() {
		b1 = new JButton("foo");
		b1.addActionListener((ActionEvent e) -> System.out.println("b1"));
		b2 = new JButton("bar");
		b2.addActionListener((ActionEvent e) -> System.out.println("b2"));
	}
}


