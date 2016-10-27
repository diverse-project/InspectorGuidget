
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
class Q {
	Q() {
		JMenuItem menuItem = new JMenuItem();
		menuItem.addActionListener((ActionEvent e) -> System.out.println("foo"));
	}
}

