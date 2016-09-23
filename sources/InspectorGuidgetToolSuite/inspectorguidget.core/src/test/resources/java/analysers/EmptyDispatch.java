import javax.swing.*;
import java.awt.event.*;

class Foo {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("foo")) {
					action(e.getSource());
				}
			}
		});
	}

	public void action(final Object source) {
		// Empty dispatch.
	}
}

