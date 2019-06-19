
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class D {
	JButton but1;
	JButton but2;
	D() {
		but1 = new JButton();
		but1.addActionListener((ActionEvent e) -> System.out.println("but1"));
		but2 = new JButton();
		but2.addActionListener((ActionEvent e) -> {
			if ((e.getModifiers()) == 0) {
				return;
			}
			System.out.println("but2");
		});
	}
}

