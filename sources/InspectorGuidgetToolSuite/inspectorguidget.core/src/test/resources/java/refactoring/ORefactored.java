package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class O {
	JButton but1;
	JButton but2;
	int foo;
	int bar;
	O() {
		foo = 1;
		bar = 2;
		OListener listener = new OListener(this);
		but1 = new JButton("foo1");
		but1.addActionListener((ActionEvent e) -> System.out.println(foo));
		but2 = new JButton("foo2");
		but2.addActionListener((ActionEvent e) -> System.out.println(bar));
	}
}
class OListener {
	O m;
	public OListener(final O m) {
		OListener.this.m = m;
	}
}

