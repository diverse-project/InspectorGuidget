package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

public class FindElementFilter extends AbstractFilter<CtElement> {
	final @NotNull CtElement elt;

	public FindElementFilter(final @NotNull CtElement element) {
		super(CtElement.class);
		elt = element;
	}

	@Override
	public boolean matches(final CtElement element) {
		return element==elt;
	}
}