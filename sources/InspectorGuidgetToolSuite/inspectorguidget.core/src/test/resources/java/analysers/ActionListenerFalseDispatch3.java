package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

class Foo {
	JPanel b;

	public void foo() {
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isPopupTrigger()) {
					bar(e);
				}
			}

			private void bar(MouseEvent e) {
				if(e.isAltDown()) {
					System.out.println("bar");
				}
			}
		});
	}
}
