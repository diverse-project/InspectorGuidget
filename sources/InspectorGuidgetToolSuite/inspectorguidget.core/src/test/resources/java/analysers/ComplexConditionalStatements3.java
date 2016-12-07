package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class D implements ActionListener {
	JButton but1;
	JButton but2;
	JButton but3;

	D() {
		but1 = new JButton();
		but1.addActionListener(this);
		but2 = new JButton();
		but2.addActionListener(this);
		but3 = new JButton();
		but3.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(Objects.equals(src, but1)) {
			System.out.println("cmd1");
		}else {
			if(Objects.equals(src, but2)) {
				if(src==but3) {
					System.out.println("cmd2");
					return;
				}
				System.out.println("cmd3");
			}
		}
	}
}