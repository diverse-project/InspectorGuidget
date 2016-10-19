
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class A {
	JButton but;
	A() {
		but = new JButton("foo");
		but.setActionCommand("FOO");
		but.addActionListener((ActionEvent e) -> System.out.println("coucou"));
	}
}

