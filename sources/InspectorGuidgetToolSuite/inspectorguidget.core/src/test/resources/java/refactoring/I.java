package java.refactoring;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class I implements ActionListener {
	JButton but1;
	JButton but2;

	I() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);

		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Object o = e.getSource();

		if(o instanceof JButton) {
			String actionCmd = ((JButton)o).getActionCommand();

			if(actionCmd.equals("FOO")) {
				System.out.println("coucou1");
				return;
			}
			if(actionCmd.equals("BAR")) {
				System.out.println("coucou2");
				return;
			}
		}
	}
}
