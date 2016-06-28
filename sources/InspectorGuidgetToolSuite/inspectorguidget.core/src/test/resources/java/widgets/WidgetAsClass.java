package fr.inria.diverse.torgen.inspectorguidget.test;

import javafx.scene.control.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo {
	private Bar bar;

	public void foofoo() {
		if(bar==null) {
			bar = new Bar();
			bar.setTitle(Bar.barbar);
			bar.setTitle(Bar.getbarbar());
		}
		bar.setVisible(true);

		Bar bb = bar;
		bb.setTitle(Bar.barbar);
	}
}


class Bar extends JFrame {
	String barb = "barb";
	static final String barbar = "bbbarr";
	static final String getbarbar() {
		return "barbabr";
	}

	public String foooooo() {
		return this.barb;
	}
}
