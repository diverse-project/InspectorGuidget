package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	public void foo() {
		List<FooFoo> l=new ArrayList<>();
		l.add((evt) -> {
			System.out.println("action list lambda with default static methods and inheritance");
		});
	}
}


interface FooFoo extends ActionListener {
	default void barbar() {
		System.out.println("default method");
	}

	static void bar() {
		System.out.println("static method");
	}
}
