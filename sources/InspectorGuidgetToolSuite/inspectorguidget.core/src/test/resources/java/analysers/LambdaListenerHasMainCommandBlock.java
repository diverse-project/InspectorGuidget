package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.*;
import java.util.*;

class Foo {
	public void foo() {
		final ActionListener removeActionListenter = arg0 -> {
			List<String> values = new ArrayList<>();
			List<String> values2 = new ArrayList<>();

			for (String val : values) {
				values2.remove(val);
			}
		};
	}
}
