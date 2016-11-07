
package java.refactoring;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
class U extends JFrame {
	JButton but1;
	U() {
		but1 = new JButton("foo1");
		but1.addActionListener((ActionEvent e) -> System.out.println("foo"));
	}
}
