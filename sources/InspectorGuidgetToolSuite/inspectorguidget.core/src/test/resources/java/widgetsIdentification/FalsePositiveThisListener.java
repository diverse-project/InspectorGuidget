import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo extends JButton implements ActionListener {

	public Foo() {
		super("foo");
		foooo(this);
	}

	public void foooo(JButton bu) {
		bu.setName("butttton");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource()); // Command 1
	}
}
