import javax.swing.*;
import java.awt.event.*;


public class MenuWidgetAndListener extends JPopupMenu implements ActionListener {
	public static final String LABEL_COPY 	= "LaTeXDrawFrame.40";

	public static final String LABEL_CUT	= "LaTeXDrawFrame.44";

	public static final String LABEL_PASTE 	= "LaTeXDrawFrame.43";

	protected JMenuItem copyM;

	protected JMenuItem cutM;

	protected JMenuItem pasteM;

	public MenuWidgetAndListener() {
		copyM = new JMenuItem("LaTeXDrawFrame.40");
		copyM.setActionCommand(LABEL_COPY);
		copyM.addActionListener(this);
		add(copyM);
		cutM = new JMenuItem("LaTeXDrawFrame.44");
		cutM.setActionCommand(LABEL_CUT);
		cutM.addActionListener(this);
		add(cutM);
		pasteM = new JMenuItem("LaTeXDrawFrame.43");
		pasteM.setActionCommand(LABEL_PASTE);
		pasteM.addActionListener(this);
		add(pasteM);
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if(o instanceof JMenuItem)
		{
			String actionCmd = ((JMenuItem)o).getActionCommand();

			if(actionCmd.equals(LABEL_COPY))
			{
				System.out.println("copy");
				return ;
			}

			if(actionCmd.equals(LABEL_CUT))
			{
				System.out.println("cut");
				return ;
			}

			if(actionCmd.equals(LABEL_PASTE))
			{
				System.out.println("paste");
				return ;
			}
		}
	}
}
