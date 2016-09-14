package foo.bar;

import java.awt.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


class AA implements ChangeListener {
	protected JSpinner a;
	public static final String A = "fdfd";


	public AA() {
		a = new JSpinner();
		a.setName(A);
		a.setEditor(new JSpinner.NumberEditor(a, "0.00"));//$NON-NLS-1$
		a.addChangeListener(this);
		a.setEnabled(false);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		Object o = e.getSource();

		if(o instanceof JSpinner) {
			String name = ((JSpinner) o).getName();

			if(name.equals(A)) {
				System.out.println("coucou");
				return;
			}
		}
	}
}


class BB implements ChangeListener {
	protected JSpinner a;
	public static final String B = "fddsdsfd";


	public BB() {
		a = new JSpinner();
		a.setName(B);
		a.setEditor(new JSpinner.NumberEditor(a, "0.00"));//$NON-NLS-1$
		a.addChangeListener(this);
		a.setEnabled(false);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		Object o = e.getSource();

		if(o instanceof JSpinner) {
			String name = ((JSpinner) o).getName();

			if(name.equals(B)) {
				System.out.println("coucou");
				return;
			}
		}
	}
}
