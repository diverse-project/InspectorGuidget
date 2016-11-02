
package java.refactoring;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
class S1 {
	protected JFileChooser fileChooser;
	javax.swing.JButton but1;
	S1() {
		SListener listener = new SListener();
		but1 = new javax.swing.JButton("foo1");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		but1.addActionListener((java.awt.event.ActionEvent e) -> {
			if ((fileChooser.showDialog(listener, "foo")) == (JFileChooser.APPROVE_OPTION)) {
				java.lang.System.out.println("foo");
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

