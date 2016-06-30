package fr.inria.diverse.torgen.inspectorguidget;

import org.junit.Ignore;
import org.junit.Test;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


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
		launcher.addInputResource("src/test/resources/java/widgetsIdentification/LambdaOnFieldWidgetsEqualCond.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtExecutable<?>>(CtExecutable.class) {
			@Override
			public boolean matches(CtExecutable<?> exec) {
				final List<CtParameterReference<?>> guiParams = exec.getParameters().stream().map(param -> param.getReference()).collect(Collectors.toList());

				if(guiParams.size()!=1) return false;

				final CtParameterReference<?> param = guiParams.get(0);

				exec.getBody().getElements(new TypeFilter<CtParameterReference<?>>(CtParameterReference.class) {
					@Override
					public boolean matches(CtParameterReference<?> p) {
						assertEquals(p, param);
						return super.matches(p);
					}
				});

				return super.matches(exec);
			}
		});
	}


//	@Test
//	@Ignore
//	public void testDeclarationOfVariableReference() throws Exception {
//		final Launcher launcher = new Launcher();
//		launcher.addInputResource("./src/test/resources/noclasspath/Foo2.java");
//
//	}
}
