
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
class A {
	javax.swing.JButton button2;
	private B theb1;
	A(B b) {
		theb1 = b;
		button2 = new javax.swing.JButton();
		button2.addActionListener((ActionEvent e) -> theb1.bb());
	}
}
class B {
	A a = new A(B.this);
	public void bb() {
	}
	public A getA() {
		return a;
	}
}
class C {
	private B theb1;
	private B theb2;
	C(B b) {
		theb2 = b;
		JMenuItem menuItem2 = new JMenuItem();
		theb1 = b;
		menuItem2.addActionListener((ActionEvent e) -> theb1.bb());
	}
}

