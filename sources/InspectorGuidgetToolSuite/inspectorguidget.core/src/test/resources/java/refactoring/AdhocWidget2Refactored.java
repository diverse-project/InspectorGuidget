package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class A {
	JButton but1;
	JButton but2;
	MyB but3;
	MyB but4;

	A() {
		but1 = new JButton();
		but1.addActionListener((ActionEvent e) -> System.out.println("coucouA"));
		but2 = new MyB("foo2", MyEnum.B.toString(), (ActionEvent e) -> System.out.println("coucouB"));
		but3 = new MyB("foo3", MyEnum.C.toString(), (ActionEvent e) -> System.out.println("coucouC"));
		but4 = new MyB("foo4", null, (ActionEvent e) -> System.out.println("coucouD"));

	}

	private static class MyB extends JButton {
		public MyB(String text, String command, ActionListener listener) {
			super(text);
			setActionCommand(command);
			addActionListener(listener);
		}
	}

	enum MyEnum {
		A, B, C;
	}
}
