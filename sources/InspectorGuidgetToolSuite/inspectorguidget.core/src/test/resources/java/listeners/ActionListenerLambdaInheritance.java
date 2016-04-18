package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


interface FooFoo extends ActionListener {
	default void fooo() {
		System.out.println("default method");
	}
}

class Foo {
	public void foo() {
		List<FooFoo> l=new ArrayList<>();
		l.add((evt) -> {
			System.out.println("action list lambda");
		});
	}
}
