import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Foooooo implements ActionListener {
	@Override
	public void actionPerformed(final ActionEvent e) {
		String path = "foufou";
		if(path == null) {
			Object parent = null;
			path = parent.toString();
			parent = parent;
		}
	}
}
