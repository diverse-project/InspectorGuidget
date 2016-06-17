import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


class Bar implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		if("foo".equals(e.getActionCommand())) {
			System.out.println("FOO: " + e.getSource()); // Command 1
			return;
		}else if("bar".equals(e.getActionCommand())) {//DO NOT REMOVE THE ELSE (USED IN TESTS)
			System.out.println("BAR: " + e.getSource()); // Command 2
			return;
		}
	}
}

class Foo {
	JButton bar = new JButton();
	JButton foo = new JButton();
	final Bar listener;

	Foo() {
		listener = new Bar();
		initWidgets();
	}

	private void initWidgets() {
		bar.setActionCommand("bar");
		foo.setActionCommand("foo");
		bar.addActionListener(listener);
		foo.addActionListener(listener);
	}
}
