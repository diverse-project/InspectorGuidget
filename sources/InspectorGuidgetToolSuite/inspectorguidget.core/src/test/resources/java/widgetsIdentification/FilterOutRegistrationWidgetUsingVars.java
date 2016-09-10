
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

class Foooo extends JDialog implements ChangeListener {
	public static final String LABEL_WIDTH = "zeioroeri";
	public static final String LABEL_HEIGHT = "oijdif";
	public static final String LABEL_CENTER_X = "azazaz";
	public static final String LABEL_CENTER_Y = "uyuyuyu";

	protected JSpinner baseCenterXField;
	protected JSpinner baseCenterYField;
	protected JSpinner widthField;
	protected JSpinner heightField;


	Foooo()
	{
		baseCenterXField = new JSpinner();
		baseCenterXField.addChangeListener(this);
		baseCenterXField.setName(LABEL_CENTER_X);

		baseCenterYField = new JSpinner();
		baseCenterYField.addChangeListener(this);
		baseCenterYField.setName(LABEL_CENTER_Y);

		widthField = new JSpinner();
		widthField.addChangeListener(this);
		widthField.setName(LABEL_WIDTH);

		heightField = new JSpinner();
		heightField.addChangeListener(this);
		heightField.setName(LABEL_HEIGHT);
	}


	@Override
	public void stateChanged(ChangeEvent e)
	{
		Object o = e.getSource();

		if(o instanceof JSpinner)
		{
			String name = ((JSpinner)o).getName();

			if(name.equals(LABEL_WIDTH))
			{
				System.out.println("coucou");
				return ;
			}
		}
	}
}
