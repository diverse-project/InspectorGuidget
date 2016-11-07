package java.refactoring;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class U extends JFrame implements ActionListener {
	JButton but1;

	U() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String cmd = e.getActionCommand();

		try {
			if(cmd.equals("FOO")) {
				System.out.println("foo");
				return;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.err.println("incorrect command: " + cmd);
		}
	}
}
