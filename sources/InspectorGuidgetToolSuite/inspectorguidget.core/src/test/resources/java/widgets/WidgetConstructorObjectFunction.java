package foo;

import foobar.WidgetConstructorObjectFunction2;

import javax.swing.*;

abstract class FooAbs {
	protected JTextField getter2() {
		return new JTextField();
	}
}

class Foo extends FooAbs{

	final Object attr;

	final Object attr2;

	final Object attr3;

	final JTextField attr4;

	Foo() {
		attr = new WidgetConstructorObjectFunction2().getter();

		WidgetConstructorObjectFunction2 b = new WidgetConstructorObjectFunction2();
		attr2 = b.getter();

		attr3 = getter2();

		attr4 = getter2();
	}
}
