package foo;

import javax.swing.event.*;

class MyListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent lse) {
		try {
			if(lse.getValueIsAdjusting()) {
				System.out.println("coucou1");
				return;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
