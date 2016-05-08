package fr.inria.diverse.torgen.inspectorguidget.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

abstract class Bar {
	protected void delegation2(ActionEvent e) {
		if(e.getSource() instanceof JButton) {
			System.out.println(((JButton)e.getSource()).getName()); // Command 2
		}
	}
}

class Foo extends Bar implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		delegation(e.getActionCommand());

		delegation2(e);
	}

	private void delegation(final String actionCmd) {
		if("fooo".equals(actionCmd)) {
			System.out.println(actionCmd); // Command 1
		}
	}
}
