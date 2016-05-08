package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	public final String foo = "foo";

	public boolean isItOkForYou() {
		return true;
	}

	public void foo() {
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isItOkForYou()) {
					if("test".equals(foo)) {// Condition 1 of the commands 1 and 2
						// The execution of the command requires the two if statements.
						if(e.getSource() instanceof JButton) { // Condition 2 of the command 1
							System.out.println(((JButton) e.getSource()).getName());// command 1
							return;
						}
						if(e.getSource() instanceof JMenuBar) { // Condition 2 of the command 2
							System.out.println(((JMenuBar) e.getSource()).getName());// command 2
							return;
						}
					}
				}
			}
		});
	}
}
