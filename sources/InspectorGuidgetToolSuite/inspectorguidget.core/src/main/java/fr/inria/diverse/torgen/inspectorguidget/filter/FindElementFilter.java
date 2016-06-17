package fr.inria.diverse.torgen.inspectorguidget.filter;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

public class FindElementFilter extends AbstractFilter<CtElement> {
	final @NotNull CtElement elt;
	final boolean identify;

	public FindElementFilter(final @NotNull CtElement element, final boolean identifyCheck) {
		super(CtElement.class);
		elt = element;
		identify = identifyCheck;
	}

	@Override
	public boolean matches(final CtElement element) {
		return identify ? element==elt : element.equals(elt);
	}
}