
package fr.inria.diverse.torgen.inspectorguidget.test;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class F {
	JButton b1 = new JButton();
	JButton b2 = new JButton();
	public F() {
		b1 = new JButton("foo");
		b1.addActionListener((ActionEvent e) -> dispatch1());
		b2 = new JButton("bar");
		b2.addActionListener((ActionEvent e) -> dispatch2());
	}
	private void dispatch1() {
		System.out.println("dispatch foo");
	}
	private void dispatch2() {
		System.out.println("dispatch bar");
	}
}

