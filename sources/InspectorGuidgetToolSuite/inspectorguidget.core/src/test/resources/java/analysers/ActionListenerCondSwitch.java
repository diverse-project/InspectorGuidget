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
				switch(e.getActionCommand()) {
					case "foo":
						System.out.println("foo");
						break;
					case "bar":
						System.out.println("bar");
						return;
					case "foobar":
						System.out.println("foobar");
						break;
					case "nope": return;
					default: break;
				}
			}
		});
	}
}
