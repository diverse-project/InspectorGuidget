package fr.inria.diverse.torgen.inspectorguidget.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

class SWTEventHandlerLambda {
	public void foo() {
		new Button(null, SWT.NONE).addListener(SWT.Selection,
			evt ->
				System.out.println("JFX handler with lambda"));
	}
}
