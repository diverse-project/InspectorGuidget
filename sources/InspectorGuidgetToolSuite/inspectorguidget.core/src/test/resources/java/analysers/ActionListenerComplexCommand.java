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
				String bar1 = ""; // Shared by the two commands

				if(e == b) {
					bar1 = "button"; // Specific to command 1
				} else if(e.getSource() instanceof JPasswordField) {
					bar1 = "pass"; // Specific to command 2
				} else return;

				Bar bar = new Bar(); // Shared by the two commands
				bar.bar(bar1); // Shared by the two commands

				//TODO same version but with a switch statement.
			}
		});
	}
}


class Bar {
	public void bar(String bar) {

	}
}
