import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MenuListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(src instanceof AbstractButton) {
			AbstractButton ab = ((AbstractButton) src);
			String label = ab.getActionCommand();

			if(label.equals(LaTeXDrawResources.LABEL_SAVE)) {
				System.out.println("MenuListener.save(false)");
				return;
			}
		}
	}
}

class ToolbarListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(src instanceof AbstractButton) {
			AbstractButton ab = ((AbstractButton) src);
			String label = ab.getActionCommand();

			if(label.equals(LaTeXDrawResources.LABEL_SAVE)) {
				System.out.println("ToolbarListener.save(false)");
				return;
			}
		}
	}
}


class LaTeXDrawResources {
	public static final String LABEL_SAVE = "LABEL_SAVE";
}

class Toobar {
	Toobar(MenuListener ml, ToolbarListener tbl) {

		JButton button = new JButton();
		button.addActionListener(ml);
		button.setActionCommand(LaTeXDrawResources.LABEL_SAVE);

		JButton button2 = new JButton();
		button2.addActionListener(tbl);
		button2.setActionCommand(LaTeXDrawResources.LABEL_SAVE);
	}
}