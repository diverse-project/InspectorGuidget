package fr.inria.diverse.torgen.inspectorguidget.filter;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;

/**
 * A filter that identifies the type refs that are part of or a sub-type of a type ref part of a given set of type refs.
 */
public class SingleTypeRefFilter extends AbstractFilter<CtTypeReference<?>> {
	private final @NotNull CtTypeReference<?> type;

	/**
	 * Creates the filter.
	 * @param type The set of type refs used to find visited type refs
	 */
	public SingleTypeRefFilter(final @NotNull CtTypeReference<?> type) {
		super(CtTypeReference.class);
		this.type = type;
	}

	@Override
	public boolean matches(final CtTypeReference<?> element) {
		return element.equals(type);
	}
}