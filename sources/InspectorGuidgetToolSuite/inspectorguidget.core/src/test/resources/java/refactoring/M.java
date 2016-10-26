package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class MListener implements ActionListener {
	M m;

	public MListener(final M m) {
		this.m = m;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		String cmd = e.getActionCommand();

		if(cmd.equals("FOO")) {
			m.foo();
			return;
		}
		if(cmd.equals("BAR")) {
			m.bar();
			return;
		}
	}
}

class M {
	JButton but1;
	JButton but2;
	M() {
		MListener listener = new MListener(this);
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(listener);
		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(listener);
	}
	public void foo() {
	}
	public void bar() {
	}
}
