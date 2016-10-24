
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class D {
	JButton but1;
	JButton but2;
	D() {
		but1 = new JButton("foo1");
		but1.addActionListener((ActionEvent e) -> {
			Object bar = e.getSource();
			System.out.println(bar);
		});
		but2 = new JButton("foo2");
		but2.addActionListener((ActionEvent e) -> {
			Object bar = e.getSource();
			System.out.println(bar);
		});
	}
}

