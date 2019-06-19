
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
class R1 {
	protected JFileChooser fileChooser;
	JButton but1;
	R1() {
		but1 = new JButton("foo1");
		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileChooser.setMultiSelectionEnabled(false);
		but1.addActionListener((ActionEvent e) -> {
			int id = this.fileChooser.showDialog(null, "foo");
			if (id == (JFileChooser.APPROVE_OPTION)) {
				System.out.println("foo");
			}
		});
	}
}
class R2 {
	protected JFileChooser fileChooser;
	JButton but2;
	R2() {
		but2 = new JButton("foo2");
		this.fileChooser = new JFileChooser();
		this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fileChooser.setMultiSelectionEnabled(false);
		but2.addActionListener((ActionEvent e) -> {
			int id = this.fileChooser.showDialog(null, "bar");
			if (id == (JFileChooser.APPROVE_OPTION)) {
				System.out.println("bar");
			}
		});
	}
}
class RListener {
	protected JFileChooser fileChooser;
	public RListener() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
	}
}

