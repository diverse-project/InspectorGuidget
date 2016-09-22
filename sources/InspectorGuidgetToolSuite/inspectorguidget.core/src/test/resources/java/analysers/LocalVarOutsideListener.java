package foo;

import javax.swing.*;
import java.awt.event.*;

class Foo {
	public void foo() {
		JPanel b = new JPanel();
		b.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				final int keyCode = e.getKeyCode();
				final String foo = "foo";

				if(e.isControlDown()) {
					switch(keyCode) {
						case KeyEvent.VK_UP:
							e.consume();
							if(foo != null) {
								System.out.println("up");
							}
							break;
						case KeyEvent.VK_DOWN:
							e.consume();
							if(foo != null) {
								System.out.println("down");
							}
							break;
						default:
							break;
					}
				}else if(keyCode == KeyEvent.VK_ENTER) {
					System.out.println("coucou");
				}
			}
		});
	}
}
