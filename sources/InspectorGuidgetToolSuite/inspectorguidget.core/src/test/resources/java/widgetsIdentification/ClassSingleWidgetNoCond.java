import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo implements ActionListener {
	JButton fooo = new JButton();

	public Foo() {
		fooo.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getSource()); // Command 1
	}
}
