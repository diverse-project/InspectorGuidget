package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Bar {
	private JButton button = new JButton();

	public JButton getButton() {
		return button;
	}
}

class Foo {
	Bar bar = new Bar();

	public void foo() {
		bar.getButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(e);
			}
		});
	}
}
