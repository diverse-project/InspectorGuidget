import javax.swing.*;

class Foo {

	Object label;

	public void method() {
		final Object foo = new JLabel();

		Object bar = foo;

		Object barbar = bar;

		this.label = barbar;
	}
}
