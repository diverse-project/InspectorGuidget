package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	JButton b = new JButton();
	public void foo() {
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() instanceof JButton) {
					System.out.println(((JButton)e.getSource()).getName()); // Command 1
					return;
				}
				if(e.getSource()==b) {
					System.out.println(b); // Command 2
					return;
				}
				if(e.getActionCommand().equals("ACTION_CMD")) {
					System.out.println(e.getActionCommand()); // Command 3
					return;
				}
			}
		});
	}
}
