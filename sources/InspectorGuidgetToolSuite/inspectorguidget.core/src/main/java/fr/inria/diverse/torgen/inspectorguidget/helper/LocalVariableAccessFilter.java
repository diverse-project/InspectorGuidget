package fr.inria.diverse.torgen.inspectorguidget.helper;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.AbstractFilter;

public class LocalVariableAccessFilter extends AbstractFilter<CtVariableReference<?>> {
	public LocalVariableAccessFilter() {
		super(CtVariableReference.class);
	}

	@Override
	public boolean matches(final CtVariableReference<?> element) {
		return element.getDeclaration() instanceof CtLocalVariable;
	}
}
