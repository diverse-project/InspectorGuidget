package java.refactoring;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SListener extends JFrame implements ActionListener {
	protected JFileChooser fileChooser;

	public SListener() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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

class S1 {
	JButton but1;
	S1() {
		SListener listener = new SListener();
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(listener);
	}
}
