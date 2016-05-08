package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AbstractFilter;

public class ConditionalFilter extends AbstractFilter<CtStatement>{
	public ConditionalFilter() {
		super(CtStatement.class);
	}

	@Override
	public boolean matches(final @Nullable CtStatement stat) {
		return isConditional(stat);
	}

	/**
	 * @param stat The element to check.
	 * @return True if the given element is a conditional statement (if, switch, ternary).
	 */
	public static boolean isConditional(final @Nullable CtElement stat) {
		return stat instanceof CtIf || stat instanceof CtSwitch<?>;
	}
}
