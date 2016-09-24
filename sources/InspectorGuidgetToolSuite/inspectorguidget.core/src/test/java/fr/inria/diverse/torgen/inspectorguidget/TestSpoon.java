package fr.inria.diverse.torgen.inspectorguidget;

import org.junit.Ignore;
import org.junit.Test;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.stream.IntStream;


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

		launcher.getModel().getElements(new TypeFilter<CtClass<?>>(CtClass.class)).forEach(cl -> {
			final long t = IntStream.range(0, 20).mapToLong(i -> {
				long time = System.currentTimeMillis();
				System.out.print(cl.getAllMethods().isEmpty());
				return System.currentTimeMillis() - time;
			}).sum();

			System.out.println("\ngetAllMethods in: " + t);
		});
	}
}
