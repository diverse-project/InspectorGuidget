import javax.swing.*;
import java.awt.event.*;

class Bar implements ActionListener{
	@Override
	public void actionPerformed(final ActionEvent e) {
	}
}


class Foo extends Bar {
	JButton foob = new JButton();
	JButton foob2 = new JButton();

	public Foo() {
		super();
		foob = new JButton();
		foob.addActionListener((ActionEvent e) -> System.out.println("foob"));
		foob2 = new JButton();
		foob2.addActionListener((ActionEvent e) -> System.out.println("foob2"));
	}
}

