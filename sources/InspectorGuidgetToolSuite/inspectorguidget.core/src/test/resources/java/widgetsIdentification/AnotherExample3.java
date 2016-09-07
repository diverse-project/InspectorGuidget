import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MenusListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if(src instanceof AbstractButton) {
			AbstractButton ab = ((AbstractButton) src);
			String label = ab.getActionCommand();

			if(label.equals(LaTeXDrawResources.LABEL_SAVE)) {
				System.out.println("mainFrame.save(false)");
				return;
			}

			if(label.equals(LaTeXDrawResources.LABEL_SAVE_AS)) {
				System.out.println("mainFrame.save(true)");
				return;
			}
		}
	}
}


class LaTeXDrawResources {
	public static final String LABEL_SAVE_AS = "LABEL_SAVE_AS";
	public static final String LABEL_SAVE = "LABEL_SAVE";
}

class Toobar {
	Toobar(MenusListener ml) {
		JButton button;

		button = new JButton();
		button.addActionListener(ml);
		button.setActionCommand(LaTeXDrawResources.LABEL_SAVE);

		button = new JButton();
		button.addActionListener(ml);
		button.setActionCommand(LaTeXDrawResources.LABEL_SAVE_AS);

		JButton button2 = new JButton();
	}
}