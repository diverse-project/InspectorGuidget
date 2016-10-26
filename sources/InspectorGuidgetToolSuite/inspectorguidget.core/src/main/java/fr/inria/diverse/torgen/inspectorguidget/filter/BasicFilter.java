package fr.inria.diverse.torgen.inspectorguidget.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

public class BasicFilter<T extends CtElement> extends AbstractFilter<T> {
	public BasicFilter(final Class<? super T> type) {
		super(type);
	}
}
