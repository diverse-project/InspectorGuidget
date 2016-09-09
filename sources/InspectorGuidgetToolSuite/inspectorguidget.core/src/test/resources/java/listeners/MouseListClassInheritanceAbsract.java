package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

abstract class Foo implements MouseListener {
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}


class Foo2 extends Foo {
}