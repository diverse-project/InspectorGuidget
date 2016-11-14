import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.event.*;

class Bar implements ActionListener{
	private static final Logger LOG = Logger.getAnonymousLogger();

	JButton barb = new JButton();

	public Bar() {
		barb = new JButton();
		barb.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==barb) {
			throw new IllegalArgumentException();
		}
		if("foob2".equals(e.getActionCommand())) {
			LOG.log(Level.SEVERE, "foo");
			return;
		}
	}
}


class Foo extends Bar {
	private static final Logger LOG = Logger.getAnonymousLogger();

	JButton foob = new JButton();
	JButton foob2 = new JButton();

	public Foo() {
		super();
		foob = new JButton();
		foob.addActionListener(this);
		foob2 = new JButton();
		foob2.setActionCommand("foob2");
		foob2.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource()==foob) {
			LOG.log(Level.SEVERE, "foo");
			throw new IllegalArgumentException();
		}else {
			LOG.log(Level.SEVERE, "foo");
			super.actionPerformed(e);
		}
	}
}

