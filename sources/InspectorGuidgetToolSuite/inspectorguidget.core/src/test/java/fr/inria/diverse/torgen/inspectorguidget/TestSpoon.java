package fr.inria.diverse.torgen.inspectorguidget;

import org.junit.Ignore;
import org.junit.Test;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertNotEquals;


class TestProcessor extends AbstractProcessor<CtExecutable<?>> {
	@Override
	public void process(CtExecutable<?> element) {
//		System.out.println(element.getVariable().getDeclaration());
	}
}

public class TestSpoon {
	@Test
	@Ignore
	public void testSpoon() {
		final spoon.Launcher launcher = new spoon.Launcher();
		launcher.addInputResource("src/test/resources/java/SpoonSnippet.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		List<CtVariable<?>> vars = launcher.getModel().getElements(new TypeFilter<CtVariable<?>>(CtVariable.class) {
//			@Override
//			public boolean matches(CtVariable<?> var) {
//				return true;
//			}
		});

		assertNotEquals(vars.get(0), vars.get(1));
	}


//	@Test
//	@Ignore
//	public void testDeclarationOfVariableReference() throws Exception {
//		final Launcher launcher = new Launcher();
//		launcher.addInputResource("./src/test/resources/noclasspath/Foo2.java");
//
//	}
}
