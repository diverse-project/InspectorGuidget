package fr.inria.diverse.torgen.inspectorguidget.test;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SWTEventHandlerClass implements Listener {
	@Override
	public void handleEvent(Event event) {
		System.out.println("swt handler as a class.");
	}
}
