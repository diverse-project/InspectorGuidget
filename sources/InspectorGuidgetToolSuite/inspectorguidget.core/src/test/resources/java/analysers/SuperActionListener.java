import javax.swing.*;
import java.awt.event.*;

class Bar implements ActionListener{
	JButton barb = new JButton();

	public Bar() {
		barb = new JButton();
		barb.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==barb) {
			System.out.println("barb");
			return;
		}
		if("foob2".equals(e.getActionCommand())) {
			System.out.println("foob2");
			return;
		}
	}
}


class Foo extends Bar {
	JButton foob = new JButton();
	JButton foob2 = new JButton();

	public Foo() {
		super();
		foob = new JButton();
		foob.addActionListener(this);
		foob2 = new JButton();
		foob2.setActionCommand("foob2");
		foob2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==foob) {
			System.out.println("foob");
		}else {
			super.actionPerformed(e);
		}
	}
}

