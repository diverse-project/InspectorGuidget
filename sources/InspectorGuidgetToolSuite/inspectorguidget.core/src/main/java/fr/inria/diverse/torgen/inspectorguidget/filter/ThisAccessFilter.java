package fr.inria.diverse.torgen.inspectorguidget.filter;

import spoon.reflect.code.CtThisAccess;
import spoon.reflect.visitor.filter.AbstractFilter;

/**
 * A filter to selects explicit or implicit 'this' usages.
 */
public class ThisAccessFilter extends AbstractFilter<CtThisAccess<?>> {
	private final boolean implicit;

	public ThisAccessFilter(final boolean isImplicit) {
		super();
		implicit = isImplicit;
	}

	@Override
	public boolean matches(final CtThisAccess<?> element) {
		return implicit == element.isImplicit() && super.matches(element);
	}
}
