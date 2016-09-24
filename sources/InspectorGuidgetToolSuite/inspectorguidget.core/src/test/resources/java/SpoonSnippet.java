package foo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

abstract class Foo extends JDialog {

}


class Bar extends JFrame implements ActionListener {
	public Bar() {
	}

	public void actionPerformed(ActionEvent e){
	}
}

class Bar2 extends JFrame implements ActionListener {
	public Bar2() {
	}

	public void actionPerformed(ActionEvent e){
	}
}
