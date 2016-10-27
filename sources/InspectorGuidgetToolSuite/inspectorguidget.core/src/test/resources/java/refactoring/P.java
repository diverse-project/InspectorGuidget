package java.refactoring;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//class Res {
//	public static final String ACTION_CMD = "FOO";
//}

class B {//} implements ActionListener {
	A a = new A(this);

//	@Override
//	public void actionPerformed(final ActionEvent e) {
//		if(e.getActionCommand().equals(Res.ACTION_CMD)) {
//			System.out.println("foo");
//			return;
//		}
//	}

	public void bb() {

	}

	public A getA() {
		return a;
	}
}

class A implements ActionListener{
//	JButton button;
	JButton button2;
	private B theb1;

	A(B b) {
		theb1 = b;
//		button = new JButton();
//		button.setActionCommand(Res.ACTION_CMD);
//		button.addActionListener(theb1);

		button2 = new JButton();
		button2.setActionCommand("fooo");
		button2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals("fooo")) {
			theb1.bb();
			return;
		}
	}
}

class C {
	private B theb2;

	C(B b) {
		theb2 = b;
//		JMenuItem menuItem = new JMenuItem();
//		menuItem.setActionCommand(Res.ACTION_CMD);
//		menuItem.addActionListener(theb2);

		JMenuItem menuItem2 = new JMenuItem();
		menuItem2.setActionCommand("fooo");
		menuItem2.addActionListener(theb2.getA());
	}
}
