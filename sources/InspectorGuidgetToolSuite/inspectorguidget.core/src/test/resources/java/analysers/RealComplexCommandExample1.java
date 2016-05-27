package java.analysers;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

class DummyModelll {
	public Shape get1() {
		return new Rectangle();
	}
}


public class RealComplexCommandExample1 implements ChangeListener {
	public static final String LABEL_ROUND_CORNER = "fooo";
	public static final String LABEL_CENTER_X = "barrr";
	public static final String LABEL_CENTER_Y = "fdsfsdfsdf";
	public static final String LABEL_RADIUS = "ezrzerzerez";

	DummyModelll dum = new DummyModelll();
	protected JSpinner radiusField = new JSpinner();
	protected JSpinner centerXField = new JSpinner();


	@Override
	public void stateChanged(ChangeEvent e) {
		try {
			Object o = e.getSource();
			Shape value = dum.get1();

			if(o instanceof JSpinner) {
				String name = ((JSpinner) o).getName();

				if(name.equals(LABEL_ROUND_CORNER)) {
					if(value instanceof Rectangle) {
						System.out.println(value);//Command 1
						((Rectangle)value).add(0, Integer.valueOf(((JSpinner)o).getValue().toString()));
					}
					return;
				}

				Point2D p1, p2;

				if(value instanceof Line2D) {
					Line2D c = (Line2D) value;
					p1 = c.getP1();
					p2 = c.getP1();
				}else {
					Arc2D s = (Arc2D) value;
					p1 = s.getEndPoint();
					p2 = s.getStartPoint();
				}

				p1.setLocation(p1.getX()*2.0, p1.getY()*2.0);

				if(name.equals(LABEL_CENTER_X)) {
					double radius = Double.valueOf(radiusField.getValue().toString());//Command 2
					double x = Double.valueOf(centerXField.getValue().toString());
					p1.setLocation(x- radius, p1.getY());
					return;
				}

				if(name.equals(LABEL_CENTER_Y)) {
					double radius = Double.valueOf(radiusField.getValue().toString());//Command 3
					double y = Double.valueOf(centerXField.getValue().toString());
					p2.setLocation(p2.getX(), y - radius);
					return;
				}

				if(name.equals(LABEL_RADIUS)) {
					double radius = Double.valueOf(radiusField.getValue().toString());//Command 4
					double y = Double.valueOf(centerXField.getValue().toString());
					p2.setLocation(p2.getX(), y - radius);
					p1.setLocation(p2.getX(), y + radius);
					return;
				}

				p1.setLocation(p1.getX()*3.0, p1.getY()*3.0);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}

//		super.stateChanged(e); //TODO
	}
}