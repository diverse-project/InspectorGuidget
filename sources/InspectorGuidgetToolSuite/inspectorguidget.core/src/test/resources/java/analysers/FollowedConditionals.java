package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.MouseAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MousenputListClass extends MouseAdapter {
	int bar = 0;

	@Override
	public void mousePressed(MouseEvent e) {
		int me = e.getButton();
		int foo = e.getClickCount();

		if (e.isPopupTrigger()) {
			me = MouseEvent.BUTTON3;
		}

		if (e.isPopupTrigger()) {
			me = MouseEvent.BUTTON3;
			foo++;
		}

		if(me==MouseEvent.MOUSE_CLICKED) {
			bar = 2;
		}

		switch (me) {
			case MouseEvent.BUTTON3:
				me = MouseEvent.BUTTON3;
				foo++;
				break;
			case MouseEvent.BUTTON1:
				bar = 1;
				break;
			case MouseEvent.BUTTON2:
				bar--;
				break;
		}
	}
}
