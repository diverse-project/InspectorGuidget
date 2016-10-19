package java.refactoring;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class H implements ActionListener {
	JButton but1;
	JButton but2;
	String foo = "fii";

	H() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener(this);

		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Object bar = e.getSource();
		String cmd = e.getActionCommand();
		if(cmd.equals("FOO")) {
			System.out.println(foo + " " + bar);
			return;
		}
		if(cmd.equals("BAR")) {
			System.out.println(bar);
			return;
		}
	}


	public void foo(String f) {
		foo = f;
		but1.setText(foo);
	}
}
