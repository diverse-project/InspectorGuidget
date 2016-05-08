package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Bar bar = new Bar(); // Part of commands 1 and 2

				if(e.getSource() instanceof JButton) { // Condition of command 1
					System.out.println(((JButton)e.getSource()).getName()); // Command 1
					bar.bar(); // Command 1
				}

				if(e.getSource() instanceof JScrollBar) { // Condition of command 2
					System.out.println(((JScrollBar)e.getSource()).getX()); // Command 2
					bar.bar(); // Command 2
				}
			}
		});
	}
}


class Bar {
	public void bar() {

	}
}
