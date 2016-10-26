
package java.refactoring;
class O {
	javax.swing.JButton but1;
	javax.swing.JButton but2;
	int foo;
	int bar;
	O() {
		foo = 1;
		bar = 2;
		but1 = new javax.swing.JButton("foo1");
		but1.addActionListener((java.awt.event.ActionEvent e) -> System.out.println(foo));
		but2 = new javax.swing.JButton("foo2");
		but2.addActionListener((java.awt.event.ActionEvent e) -> System.out.println(bar));
	}
}

