package fr.inria.diverse.torgen.inspectorguidget.helper;

import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.filter.LineFilter;

public class LinePositionFilter extends LineFilter {
	final int start;
	final int end;

	public LinePositionFilter(final int start, final int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public boolean matches(final CtStatement element) {
		return super.matches(element) && element.getPosition().getLine() > start && element.getPosition().getLine() < end;
	}
}
