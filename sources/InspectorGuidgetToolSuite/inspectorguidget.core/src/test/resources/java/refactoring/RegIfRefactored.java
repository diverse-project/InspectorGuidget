
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class A {
	JButton but;
	A() {
		if ((but) != null) {
			but.addActionListener(butCmd);
		}
	}
	private void killA() {
		if ((but) != null) {
			but.removeActionListener(butCmd);
		}
	}
	private final ActionListener butCmd = (ActionEvent e) -> System.out.println("coucou");
}

