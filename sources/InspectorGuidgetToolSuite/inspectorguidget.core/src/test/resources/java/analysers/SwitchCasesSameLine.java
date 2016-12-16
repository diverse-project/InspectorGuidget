package foo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class Foo implements ActionListener {
	JButton b1;
	JButton b2;
	JButton b3;
	JButton b4;
	JButton b5;
	JButton b6;

	public Foo() {
		b1 = new JButton();
		b1.addActionListener(this);
		b1.setActionCommand(MyEnum.ENUM1.toString());
		b2 = new JButton();
		b2.addActionListener(this);
		b2.setActionCommand(MyEnum.ENUM2.toString());
		b3 = new JButton();
		b3.addActionListener(this);
		b3.setActionCommand(MyEnum.ENUM3.toString());
		b4 = new JButton();
		b4.addActionListener(this);
		b4.setActionCommand(MyEnum.ENUM4.toString());
		b5 = new JButton();
		b5.addActionListener(this);
		b5.setActionCommand(MyEnum.ENUM5.toString());
		b6 = new JButton();
		b6.addActionListener(this);
		b6.setActionCommand(MyEnum.ENUM6.toString());
	}

	private static enum MyEnum {
		ENUM1,
		ENUM2,
		ENUM3,
		ENUM4,
		ENUM5,
		ENUM6;
	};

	@Override
	public void actionPerformed(ActionEvent ae) {
		final String command = ae.getActionCommand();

		switch (MyEnum.valueOf(command)) {
			case ENUM1:
				System.out.println("ENUM1");
				break;
			case ENUM2: case ENUM3: case ENUM4: case ENUM5:
				System.out.println("MULTI ENUM");
				break;
			case ENUM6:
				System.out.println("ENUM6");
				break;
		}
	}
}
