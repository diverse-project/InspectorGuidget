import javax.swing.*;
import java.awt.event.*;

class Foo {
	public void foo() {
		new JPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) notADispatch();
			}
		});
	}

	private void notADispatch() {
		for(int i = 0; i < 10; i++) System.out.println("dispatch command");
	}
}

