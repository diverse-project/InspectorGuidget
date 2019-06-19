package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
class S1 {
	protected JFileChooser fileChooser;
	JButton but1;
	S1() {
		SListener listener = new SListener();
		but1 = new JButton("foo1");
		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		but1.addActionListener((ActionEvent e) -> {
			if ((this.fileChooser.showDialog(listener, "foo")) == (JFileChooser.APPROVE_OPTION)) {
				System.out.println("foo");
			}
		});
	}
}
class SListener extends JFrame {
	protected JFileChooser fileChooser;
	public SListener() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
}

