package java.refactoring;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class U extends JFrame implements ActionListener {
	JButton[] but1;

	U() {
		but1 = new JButton[5];

		for(int i=0; i<but1.length; i++) {
			but1[i] = new JButton();
			but1[i].setActionCommand(String.valueOf(i));
			but1[i].addActionListener(this);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final int cmd = Integer.valueOf(e.getActionCommand());

		if(cmd % 2 == 0) {
			System.out.println("foo");
		}else {
			System.out.println("bar");
		}
	}
}
