package java.refactoring;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;

class D implements ActionListener {
	JButton but1;
	JButton but2;

	D() {
		but1 = new JButton();
		but1.addActionListener(this);
		but2 = new JButton();
		but2.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(Objects.equals(src, but1)) {
			System.out.println("but1");
		}else {
			if(Objects.equals(src, but2)) {
				if(e.getModifiers() == 0)
					return;

				System.out.println("but2");
			}
		}
	}
}