package fr.inria.diverse.torgen.inspectorguidget.test;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;


public class JFXEventHandlerClass implements EventHandler<MouseEvent> {
	@Override
	public void handle(MouseEvent event) {
		System.out.println("jfx handler as a class.");
	}
}
