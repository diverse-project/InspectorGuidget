package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.*;
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
				Object bar = e.getSource();
				String cmd = e.getActionCommand();

				if(bar instanceof JButton) {
					System.out.println(((JButton)bar).getName()); // Command 1
					return;
				}
				if(bar==b) {
					System.out.println(b); // Command 2
					return;
				}
				if("ACTION_CMD".equals(cmd)) {
					System.out.println(e.getActionCommand()); // Command 3
					return;
				}
			}
		});
	}
}
