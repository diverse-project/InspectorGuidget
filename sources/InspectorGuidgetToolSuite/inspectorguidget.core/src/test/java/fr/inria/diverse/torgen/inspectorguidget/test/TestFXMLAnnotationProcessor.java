package fr.inria.diverse.torgen.inspectorguidget.test;

import fr.inria.diverse.torgen.inspectorguidget.processor.FXMLAnnotationProcessor;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestFXMLAnnotationProcessor extends TestInspectorGuidget<FXMLAnnotationProcessor> {
	private FXMLAnnotationProcessor proc;

	@Override
	public List<FXMLAnnotationProcessor> createProcessor() {
		proc = new FXMLAnnotationProcessor();
		return Collections.singletonList(proc);
	}

	@Test
	public void testFXMLAnnotationAttributes() {
		run("src/test/resources/java/fxml/FXMLAnnotationAttributes.java");
		assertEquals(2, proc.getFieldAnnotations().size());
		assertEquals(0, proc.getMethodAnnotations().size());
	}
}
