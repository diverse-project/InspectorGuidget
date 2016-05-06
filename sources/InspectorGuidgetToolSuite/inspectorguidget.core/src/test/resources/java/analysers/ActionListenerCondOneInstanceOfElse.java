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
				if(e.getSource() instanceof JButton) {
					System.out.println(((JButton)e.getSource()).getName()); // Command 1
				}else {
					System.out.println(e.getSource()); // Command 2
				}
			}
		});
	}
}
