package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class MousenputListClass implements MouseInputListener {
	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing, just a comment

	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		/*
		Another comment
		 */
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// System.out.println("coucou");
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
