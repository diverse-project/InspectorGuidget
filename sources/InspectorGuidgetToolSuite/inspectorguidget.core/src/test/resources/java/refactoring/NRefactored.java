
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
class N {
	protected JFileChooser fileChooser;
	JButton but1;
	JButton but2;
	N() {
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
		but2 = new JButton("foo2");
		but2.addActionListener((ActionEvent e) -> {
			int id = this.fileChooser.showDialog(null, "bar");
			if (id == (JFileChooser.APPROVE_OPTION)) {
				System.out.println("bar");
			}
		});
	}
}
class NListener {
	protected JFileChooser fileChooser;
	public NListener() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
	}
}

