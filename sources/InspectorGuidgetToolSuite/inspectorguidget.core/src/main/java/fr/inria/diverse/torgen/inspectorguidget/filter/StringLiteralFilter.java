package fr.inria.diverse.torgen.inspectorguidget.filter;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.visitor.filter.AbstractFilter;

public class StringLiteralFilter extends AbstractFilter<CtLiteral<?>> {
	@Override
	public boolean matches(final CtLiteral<?> element) {
		return element!=null && element.getValue() instanceof String;
	}
}
