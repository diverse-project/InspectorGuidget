package fr.inria.diverse.torgen.inspectorguidget.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

class Foo {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();

				if("FOO".equals(cmd)) {
					System.out.println("coucouFoo");
				}else {
					int i;

					try {
						i = Integer.valueOf(cmd);
					}catch(NumberFormatException ex) {
						i = -1;
					}

					switch(i) {
						case 1:
							System.out.println("coucou1");
							break;
						case 2:
							System.out.println("coucou1");
							break;
					}
				}
			}
		});
	}
}
