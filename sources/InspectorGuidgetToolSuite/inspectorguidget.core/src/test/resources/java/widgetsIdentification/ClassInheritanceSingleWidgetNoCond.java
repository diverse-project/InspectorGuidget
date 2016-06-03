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
}

class Foo extends Bar {
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource()); // Command 1
	}
}
