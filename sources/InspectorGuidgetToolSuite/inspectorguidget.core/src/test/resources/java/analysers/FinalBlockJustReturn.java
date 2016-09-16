package foo;

import javax.swing.event.*;

class MyListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent lse) {
		if(lse.getValueIsAdjusting()) {
			return;
		}
		return;
	}
}
