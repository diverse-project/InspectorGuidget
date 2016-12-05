package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.ActionEvent;
import java.util.Optional;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	JButton b;
	public void foo() {
		b.addActionListener(evt -> {
			if(bar()) {
				System.out.println("foo");
			}
		});
	}


	private boolean bar() {
		return true;
	}
}
