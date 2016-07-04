import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Foooooooooooo extends JDialog implements ActionListener {
	public static final String LABEL_OK  = "OK";


	public Foooooooooooo() {
		JButton okB = new JButton(LABEL_OK);
		okB.setActionCommand(LABEL_OK);
		okB.addActionListener(this);
	}


	public void actionPerformed(ActionEvent e)
	{
		Object o = e.getSource();

		if(o instanceof JButton)
		{
			String msg = ((JButton)o).getActionCommand();

			if(msg.equals(LABEL_OK))
			{
				System.out.println("coucoucou");
				return ;
			}
		}
	}
}

class Barrrr {
	public static final String LABEL_OK  = "OK";

	JButton buttonOK;

	public Barrrr() {
		buttonOK = new JButton(LABEL_OK);
	}
}
