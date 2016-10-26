
package java.refactoring;
class M {
	javax.swing.JButton but1;
	javax.swing.JButton but2;
	M() {
		but1 = new javax.swing.JButton("foo1");
		but1.addActionListener((java.awt.event.ActionEvent e) -> foo());
		but2 = new javax.swing.JButton("foo2");
		but2.addActionListener((java.awt.event.ActionEvent e) -> bar());
	}
	public void foo() {
	}
	public void bar() {
	}
}
class MListener {
	M m;
	public MListener(final M m) {
		MListener.this.m = m;
	}
}

