package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener( e -> {
			System.out.println(e);
			try {
				e.getSource();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}
