package foo.bar;

import javax.swing.JButton;

class AA {
	public AA() {
		JButton a = createButton();
	}

	JButton createButton() {
		JButton b = new JButton();
		b.setName("Foo");
		return b;
	}
}
