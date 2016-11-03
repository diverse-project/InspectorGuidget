package foo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AListener implements ActionListener {
	private static final String FOO = "foo";
	private static final String BAR = "bar";

	@Override
	public void actionPerformed(ActionEvent evt) {
		final String command = evt.getActionCommand();
		if (null != command);

		switch (command) {
			case FOO:
				System.out.println("foofoo");
				break;
			case BAR:
				System.out.println("barbar");
				break;
		}
	}
}
