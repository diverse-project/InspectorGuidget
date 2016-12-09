package fr.inria.diverse.torgen.inspectorguidget.filter;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;

import java.util.List;

/**
 * A filter that identifies the type refs that are part of or a sub-type of a type ref part of a given set of type refs.
 */
public class TypeRefFilter extends AbstractFilter<CtTypeReference<?>> {
	private final @NotNull List<CtTypeReference<?>> types;

	/**
	 * Creates the filter.
	 * @param types The set of type refs used to find visited type refs
	 */
	public TypeRefFilter(final @NotNull List<CtTypeReference<?>> types) {
		super(CtTypeReference.class);
		this.types = types;
	}

	@Override
	public boolean matches(final CtTypeReference<?> element) {
		return types.parallelStream().anyMatch(type -> element==type || element.isSubtypeOf(type));
	}
}