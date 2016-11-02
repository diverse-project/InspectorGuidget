package java.refactoring;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class T extends JFrame implements ActionListener {
	JButton but1;
	protected JFileChooser fileChooser;

	T() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals("FOO")) {
			if(fileChooser.showDialog(this, "foo") == JFileChooser.APPROVE_OPTION) {
				System.out.println("foo");
			}
			return;
		}
	}
}
