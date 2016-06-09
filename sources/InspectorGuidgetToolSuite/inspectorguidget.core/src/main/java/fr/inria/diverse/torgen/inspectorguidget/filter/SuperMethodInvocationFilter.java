package fr.inria.diverse.torgen.inspectorguidget.filter;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.visitor.filter.AbstractFilter;

/**
 * A filter that gathers the invocation calls that target a super method.
 */
public class SuperMethodInvocationFilter extends AbstractFilter<CtInvocation<?>> {
	public SuperMethodInvocationFilter() {
		super(CtInvocation.class);
	}

	@Override
	public boolean matches(final CtInvocation<?> element) {
		return element.getTarget() instanceof CtSuperAccess<?>;
	}
}
