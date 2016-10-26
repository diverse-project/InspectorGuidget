package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;

class NListener implements ActionListener {
	protected JFileChooser fileChooser;

	public NListener() {
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

class N {
	JButton but1;
	JButton but2;
	N() {
		NListener listener = new NListener();
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(listener);
		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(listener);
	}
}
