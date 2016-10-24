
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class I {
	JButton but1;
	JButton but2;
	I() {
		but1 = new JButton("foo1");
		but1.addActionListener((ActionEvent e) -> System.out.println("coucou1"));
		but2 = new JButton("foo2");
		but2.addActionListener((ActionEvent e) -> System.out.println("coucou2"));
	}
}

