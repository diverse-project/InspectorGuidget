import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	JButton b = new JButton();
	public Foo() {
		b.addActionListener(e ->
			System.out.println(((JButton)e.getSource()).getName())
		);
	}
}
