import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Foo {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				methodNotDispatch();
			}
		});
	}

	private void methodNotDispatch() {
		System.out.println("dispatch command");
	}
}

