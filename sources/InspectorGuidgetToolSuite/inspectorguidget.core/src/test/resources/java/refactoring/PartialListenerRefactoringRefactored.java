package foo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
class Bar implements ActionListener {
	public static final String B1 = "B1";
	public static final String B2 = "B2";
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getActionCommand().equals(Bar.B1)) {
			System.out.println("BBBBBBB1");
		}
	}
}
class Foo extends Bar{
	JButton b3;
	public Foo() {
		b3 = new JButton();
		b3.addActionListener((ActionEvent e) -> System.out.println("BBB3"));
		JButton b2 = new JButton();
		b2.addActionListener((ActionEvent e) -> System.out.println("BBB2"));
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		switch (e.getActionCommand()) {
			case Bar.B1 :
				System.out.println("BBB1");
				super.actionPerformed(e);
				break;
			default :
				super.actionPerformed(e);
				break;
		}
	}
}
