package fr.inria.diverse.torgen.inspectorguidget.test;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

class SWTAdapterClass extends SelectionAdapter {
	@Override
	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
		System.out.println("swt handler as a class.");
	}
}
