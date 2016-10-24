
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
class A {
	javax.swing.JButton but1;
	javax.swing.JButton but2;
	A() {
		C c = new C();
		but1 = new javax.swing.JButton("foo1");
		but1.addActionListener((ActionEvent e) -> System.out.println("coucou1"));
		but2 = new javax.swing.JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener(c);
	}
}
class C implements ActionListener {
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals("BAR")) {
			System.out.println("coucou2");
			return ;
		}
	}
}

