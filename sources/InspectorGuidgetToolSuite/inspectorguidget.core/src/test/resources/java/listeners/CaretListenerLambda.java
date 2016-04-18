package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.event.CaretListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	public void foo() {
		List<CaretListener> l=new ArrayList<>();
		l.add((evt) -> {
			System.out.println("swing lambda");
		});
	}
}
