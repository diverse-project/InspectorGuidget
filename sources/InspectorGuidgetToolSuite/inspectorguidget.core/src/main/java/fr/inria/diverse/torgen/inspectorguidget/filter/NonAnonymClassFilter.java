package fr.inria.diverse.torgen.inspectorguidget.filter;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.AbstractFilter;

public class NonAnonymClassFilter extends AbstractFilter<CtClass<?>> {
	public NonAnonymClassFilter() {
		super(CtClass.class);
	}

	@Override
	public boolean matches(final CtClass<?> element) {
		return !element.isAnonymous();
	}
}
