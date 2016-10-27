
package java.refactoring;
import javax.swing.JFileChooser;
class R1 {
	protected JFileChooser fileChooser;
	javax.swing.JButton but1;
	R1() {
		but1 = new javax.swing.JButton("foo1");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		but1.addActionListener((java.awt.event.ActionEvent e) -> {
			int id = fileChooser.showDialog(null, "foo");
			if (id == (JFileChooser.APPROVE_OPTION)) {
				java.lang.System.out.println("foo");
			}
		});
	}
}
class R2 {
	protected JFileChooser fileChooser;
	javax.swing.JButton but2;
	R2() {
		but2 = new javax.swing.JButton("foo2");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		but2.addActionListener((java.awt.event.ActionEvent e) -> {
			int id = fileChooser.showDialog(null, "bar");
			if (id == (JFileChooser.APPROVE_OPTION)) {
				java.lang.System.out.println("bar");
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

