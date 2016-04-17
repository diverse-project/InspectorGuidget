package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	public void foo() {
		List<ActionListener> l=new ArrayList<>();
		l.add((evt) -> {
			System.out.println("action list lambda");
		});
	}
}
