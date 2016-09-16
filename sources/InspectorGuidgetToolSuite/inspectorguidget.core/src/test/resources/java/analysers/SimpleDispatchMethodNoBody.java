import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

abstract class Foo {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatch1();
			}
		});
	}

	protected abstract void dispatch1();
}

