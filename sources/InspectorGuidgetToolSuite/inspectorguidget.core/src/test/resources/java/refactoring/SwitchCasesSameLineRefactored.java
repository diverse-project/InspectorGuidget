package foo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class Foo {
	JButton b1;
	JButton b2;
	JButton b3;
	JButton b4;
	JButton b5;
	JButton b6;

	public Foo() {
		b1 = new JButton();
		b1.addActionListener((ActionEvent ae) -> System.out.println("ENUM1"));
		b2 = new JButton();
		b2.addActionListener((ActionEvent ae) -> System.out.println("MULTI ENUM"));
		b3 = new JButton();
		b3.addActionListener((ActionEvent ae) -> System.out.println("MULTI ENUM"));
		b4 = new JButton();
		b4.addActionListener((ActionEvent ae) -> System.out.println("MULTI ENUM"));
		b5 = new JButton();
		b5.addActionListener((ActionEvent ae) -> System.out.println("MULTI ENUM"));
		b6 = new JButton();
		b6.addActionListener((ActionEvent ae) -> System.out.println("ENUM6"));
	}

	private static enum MyEnum {
		ENUM1,
		ENUM2,
		ENUM3,
		ENUM4,
		ENUM5,
		ENUM6;
	};
}
