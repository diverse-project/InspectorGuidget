package fr.inria.diverse.torgen.inspectorguidget.helper;

import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.visitor.filter.AbstractFilter;

public class ConditionalFilter extends AbstractFilter<CtStatement>{
	public ConditionalFilter() {
		super(CtStatement.class);
	}

	@Override
	public boolean matches(CtStatement stat) {
		return stat instanceof CtIf || stat instanceof CtSwitch<?>;
	}
}
