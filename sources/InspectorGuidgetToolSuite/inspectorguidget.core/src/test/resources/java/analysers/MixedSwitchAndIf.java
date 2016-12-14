package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.MouseAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MousenputListClass extends MouseAdapter {
	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.getComponent().isEnabled()) return;

		int me = e.getButton();
//		if (e.isPopupTrigger()) me = MouseEvent.BUTTON3;
		String foo = String.valueOf(e.getX()) + " " + String.valueOf(e.getY());

		switch (me) {
			case MouseEvent.BUTTON1:
				System.out.println("coucou1");
				break;
			case MouseEvent.BUTTON2:
				if(foo!=null) {
					System.out.println("coucou2");
				}
				break;
			case MouseEvent.BUTTON3:
				System.out.println("coucou3");
				break;
			default:
				break;
		}
	}
}
