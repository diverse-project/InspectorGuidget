package java.refactoring;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RListener implements ActionListener {
	protected JFileChooser fileChooser;

	public RListener() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		String cmd = e.getActionCommand();

		if(cmd.equals("FOO")) {
			int id = fileChooser.showDialog(null, "foo");

			if(id == JFileChooser.APPROVE_OPTION) {
				System.out.println("foo");
			}
			return;
		}
		if(cmd.equals("BAR")) {
			int id = fileChooser.showDialog(null, "bar");

			if(id == JFileChooser.APPROVE_OPTION) {
				System.out.println("bar");
			}
			return;
		}
	}
}

class R1 {
	JButton but1;
	R1() {
		RListener listener = new RListener();
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(listener);
	}
}

class R2 {
	JButton but2;
	R2() {
		RListener listener = new RListener();
		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(listener);
	}
}
