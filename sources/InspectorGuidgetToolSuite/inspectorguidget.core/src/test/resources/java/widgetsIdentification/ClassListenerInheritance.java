import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


abstract class Bar implements ActionListener {
	JButton fooo = new JButton();

	public Bar() {
		fooo.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==fooo) {
			System.out.println(e.getSource()); // Command 1
		}
	}
}

class Foo extends Bar {
	JButton bar = new JButton();

	public Foo() {
		super();
		bar.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		if(e.getSource()==bar) {
			System.out.println(e.getSource()); // Command 2
		}
	}
}
