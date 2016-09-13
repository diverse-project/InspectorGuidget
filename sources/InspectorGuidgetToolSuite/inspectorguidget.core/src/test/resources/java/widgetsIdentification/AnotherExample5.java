import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

abstract class A implements ChangeListener{
	public static final String LABEL_A = "rree";
	public static final String LABEL_B = "rrdddee";

	protected JSpinner a;
	protected JSpinner b;

	protected void A() {
		a = new JSpinner();
		a.addChangeListener(this);
		a.setName(LABEL_A);
		b = new JSpinner();
		b.addChangeListener(this);
		b.setName(LABEL_B);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object o = e.getSource();
//		GridShape g = (GridShape) glimpsePanel.getGlimpseFigure();

		if(o instanceof JSpinner) {
			String name = ((JSpinner) o).getName();
			double v = Double.valueOf(((JSpinner) o).getValue().toString()).doubleValue();

			if(name.equals(LABEL_A)) {
				System.out.println("coucou");
				return;
			}

			if(name.equals(LABEL_B)) {
				System.out.println("coudfdfdcou");
				return;
			}
		}
	}


	public void setFigureFrameField()
	{
		a.removeChangeListener(this);
		b.removeChangeListener(this);


		a.addChangeListener(this);
		b.addChangeListener(this);
	}
}
