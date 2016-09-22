package foo;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

class MousenputListClass extends MouseAdapter {
	@Override
	public void mouseClicked(MouseEvent e) {
			switch (e.getPoint().x) {
				case 1:
					if (e.getPoint().y==2) {
						System.out.println("coucou");
					}
					break;
			}
	}
}
