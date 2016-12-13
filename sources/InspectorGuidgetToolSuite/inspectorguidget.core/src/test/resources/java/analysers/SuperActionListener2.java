import java.util.logging.Logger;
import javax.swing.*;
import java.awt.event.*;

class Bar extends Foo implements ActionListener{
	@Override
	public void actionPerformed(final ActionEvent e) {
		switch(e.getActionCommand()) {
			case "FOO":
				System.out.println("foo");
				break;
			case "BAR":
				System.out.println("bar");
				break;
			default:
				super.actionPerformed(e);
		}
	}
}


class Foo extends JPanel implements ActionListener {
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if ("OK".equals(command)) {
			System.out.println("OK");
		} else if ("fff".equals(command)) {
			System.out.println("fff");
		} else{
			Logger.getAnonymousLogger().warning("Bad event");
		}
	}
}

