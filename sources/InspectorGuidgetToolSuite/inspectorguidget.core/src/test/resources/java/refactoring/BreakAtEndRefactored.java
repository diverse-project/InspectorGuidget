package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

class AAAAA implements ActionListener {
	DefaultListModel<Object> model = new DefaultListModel<>();

	AAAAA() {
		JButton but = new JButton();
		but.addActionListener((ActionEvent e) -> System.out.println("coucou"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case "OK":
				for(int index = 0; index < model.getSize(); index++) {
				}
				break;
		}
	}
}
