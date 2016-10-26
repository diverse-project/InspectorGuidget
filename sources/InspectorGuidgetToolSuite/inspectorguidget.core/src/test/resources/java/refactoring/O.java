package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class OListener implements ActionListener {
	O m;

	public OListener(final O m) {
		this.m = m;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		String cmd = e.getActionCommand();

		if(cmd.equals("FOO")) {
			System.out.println(m.foo);
			return;
		}
		if(cmd.equals("BAR")) {
			System.out.println(m.bar);
			return;
		}
	}
}

class O {
	JButton but1;
	JButton but2;
	int foo;
	int bar;
	O() {
		foo = 1;
		bar = 2;
		OListener listener = new OListener(this);
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(listener);
		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(listener);
	}
}
