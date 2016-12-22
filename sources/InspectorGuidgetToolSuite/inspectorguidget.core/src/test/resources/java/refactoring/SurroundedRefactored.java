
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class A {
	JButton but1;
	JButton but2;
	A() {
		but1 = new JButton("foo1");
		but1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((A.this.toString()) != null) {
					System.out.println("coucou1");
				}
			}
		});
		but2 = new JButton("foo2");
		but2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((A.this.toString()) != null) {
					System.out.println("coucou2");
				}
			}
		});
	}
}

