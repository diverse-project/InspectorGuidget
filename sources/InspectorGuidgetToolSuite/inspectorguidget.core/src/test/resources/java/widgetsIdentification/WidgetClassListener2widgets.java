import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foooooooooooo implements ActionListener {
	JButton fooo = new JButton();
	JButton barr = new JButton();

	public Foooooooooooo() {
		fooo.addActionListener(this);
		barr.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==fooo) {
			System.out.println("FOO " + e.getSource()); // Command 1
			return;
		}
		if(e.getSource()==barr) {
			System.out.println("BAR " + e.getSource()); // Command 2
			return;
		}
	}
}
