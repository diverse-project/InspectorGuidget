import javax.swing.*;
import java.awt.event.*;

class Bar implements ActionListener{
	@Override
	public void actionPerformed(final ActionEvent e) {
	}
}


class Foo extends Bar {
	JButton foob;
	JButton foob2;

	public Foo() {
		super();
		foob = new JButton();
		foob.setActionCommand("FOO");
		foob.addActionListener(this);
		foob2 = new JButton();
		foob2.setActionCommand("FOO2");
		foob2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		switch(e.getActionCommand()) {
			case "FOO":
				System.out.println("foob");
				break;
			case "FOO2":
				System.out.println("foob2");
				break;
			default:
				super.actionPerformed(e);
		}
	}
}

