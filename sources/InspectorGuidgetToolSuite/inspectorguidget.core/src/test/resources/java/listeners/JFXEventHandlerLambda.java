package fr.inria.diverse.torgen.inspectorguidget.test;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

class JFXEventHandlerLambda {
	public void foo() {
		new Button().addEventHandler(MouseEvent.ANY, evt -> {
			System.out.println("JFX handler with lambda");
		});
	}
}
