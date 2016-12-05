package fr.inria.diverse.torgen.inspectorguidget.filter;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

public class FindElementsFilter<T extends CtElement> extends AbstractFilter<T> {
	final @NotNull Set<T> elts;

	public FindElementsFilter(final @NotNull Set<T> element) {
		super(CtElement.class);
		elts = element;
	}

	@Override
	public boolean matches(final T element) {
		return elts.contains(element);
	}
}