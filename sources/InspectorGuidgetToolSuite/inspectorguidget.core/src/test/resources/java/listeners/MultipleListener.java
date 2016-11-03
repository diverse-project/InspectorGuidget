package foo;

import javax.swing.*;
import java.awt.event.*;

class MultipleListener implements ActionListener, ItemListener {
	JButton button;
	JComboBox cb1;
	JComboBox cb2;

	MultipleListener() {
		button = new JButton();
		button.setActionCommand("foo");
		button.addActionListener(this);

		cb1 = new JComboBox();
		cb1.addItemListener(this);

		cb2 = new JComboBox();
		cb2.addItemListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		switch(evt.getActionCommand()) {
			case "foo":
				System.out.println("fooo!");
				return;
		}
	}


	@Override
	public void itemStateChanged(ItemEvent evt) {
		if (evt.getSource() == cb1) {
			System.out.println("fooo");
		} else if (evt.getSource() == cb2) {
			System.out.println("barrrr");
		}
	}
}
