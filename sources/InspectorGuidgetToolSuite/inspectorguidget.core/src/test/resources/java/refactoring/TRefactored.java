
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
class T extends JFrame {
	JButton but1;
	protected JFileChooser fileChooser;
	T() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		but1 = new JButton("foo1");
		but1.addActionListener((ActionEvent e) -> {
			if ((fileChooser.showDialog(this, "foo")) == (JFileChooser.APPROVE_OPTION)) {
				System.out.println("foo");
			}
		});
	}
}

