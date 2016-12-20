package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

class AAAAA implements ActionListener {
	DefaultListModel<Object> model = new DefaultListModel<>();

	AAAAA() {
		JButton but = new JButton();
		but.setActionCommand("AA");
		but.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "AA":
				System.out.println("coucou");
				break;
			case "OK":
				for(int index = 0; index < model.getSize(); index++) {
				}
				break;
		}
	}
}
