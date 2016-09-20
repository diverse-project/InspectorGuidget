package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.processor.ActionProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.InspectorGuidgetProcessor;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestActionProcessor extends TestInspectorGuidget<InspectorGuidgetProcessor<? extends CtElement>> {
	private ActionProcessor classProc;

	@Override
	public List<InspectorGuidgetProcessor<? extends CtElement>> createProcessor() {
		classProc = new ActionProcessor();
		return Collections.singletonList(classProc);
	}

	@Test
	public void testAbstractActionNotAListener() {
		run("src/test/resources/java/listeners/AbstractAction.java");
		assertEquals(1, classProc.getActions().size());
	}

	@Test
	public void ActionListenerCondInstanceOfReturnNotAClass() {
		run("src/test/resources/java/analysers/ActionListenerCondInstanceOfReturn.java");
		assertEquals(0, classProc.getActions().size());
	}
}
