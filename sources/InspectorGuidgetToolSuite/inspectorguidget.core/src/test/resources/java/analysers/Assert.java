import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

class Foo implements ActionListener {
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if ("OK".equals(command)) {
			System.out.println("OK");
		} else if ("fff".equals(command)) {
			System.out.println("fff");
		} else{
			assert false: "foo";
		}
	}
}

