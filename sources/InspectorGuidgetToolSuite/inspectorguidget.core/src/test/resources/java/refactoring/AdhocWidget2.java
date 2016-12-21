package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class A implements ActionListener {
	JButton but1;
	JButton but2;
	MyB but3;
	MyB but4;

	A() {
		but1 = new JButton();
		but1.setActionCommand(MyEnum.A.toString());
		but1.addActionListener(this);
		but2 = new MyB("foo2", MyEnum.B.toString(), this);
		but3 = new MyB("foo3", MyEnum.C.toString(), this);
		but4 = new MyB("foo4", null, this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		MyEnum act = MyEnum.valueOf(command);

		switch(act) {
			case A:
				System.out.println("coucouA");
				break;
			case B:
				System.out.println("coucouB");
				break;
			case C:
				System.out.println("coucouC");
				break;
		}

		if(e.getSource()==but4) {
			System.out.println("coucouD");
			return;
		}
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
