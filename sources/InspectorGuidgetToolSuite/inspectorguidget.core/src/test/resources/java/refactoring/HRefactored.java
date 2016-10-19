
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class H {
	JButton but1;
	JButton but2;
	String foo = "fii";
	H() {
		but1 = new JButton("foo1");
		but1.setActionCommand("FOO");
		but1.addActionListener((ActionEvent e) -> {
			Object bar = e.getSource();
			System.out.println((((foo) + " ") + bar));
		});
		but2 = new JButton("foo2");
		but2.setActionCommand("BAR");
		but2.addActionListener((ActionEvent e) -> {
			Object bar = e.getSource();
			System.out.println(bar);
		});
	}
	public void foo(String f) {
		foo = f;
		but1.setText(foo);
	}
}

