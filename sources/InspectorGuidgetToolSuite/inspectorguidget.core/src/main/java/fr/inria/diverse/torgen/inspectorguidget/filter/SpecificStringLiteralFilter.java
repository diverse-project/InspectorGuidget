package fr.inria.diverse.torgen.inspectorguidget.filter;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.visitor.filter.AbstractFilter;

public class SpecificStringLiteralFilter extends AbstractFilter<CtLiteral<?>> {
	private CtLiteral<?> lit;

	public SpecificStringLiteralFilter(final CtLiteral<?> lit) {
		this.lit = lit;
	}

	@Override
	public boolean matches(final CtLiteral<?> element) {
		return element.equals(lit);
	}
}
