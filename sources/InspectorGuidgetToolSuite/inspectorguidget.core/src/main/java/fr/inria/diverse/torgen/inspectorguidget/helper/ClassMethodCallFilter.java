package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.VariableAccessFilter;

import java.util.List;

public class ClassMethodCallFilter extends AbstractFilter<CtInvocation<?>>{
	private final @NotNull List<CtParameter<?>> events;
	private final @NotNull CtClass<?> listenerClass;

	public ClassMethodCallFilter(final @NotNull List<CtParameter<?>> guiEvents, final @NotNull CtClass<?> clazz) {
		super(CtElement.class);
		events = guiEvents;
		listenerClass = clazz;
	}

	@Override
	public boolean matches(final CtInvocation<?> element) {
		return listenerClass.getAllMethods().contains(element.getExecutable().getDeclaration()) &&
				element.getArguments().stream().filter(arg ->
					events.stream().filter(evt ->
						!arg.getElements(new VariableAccessFilter<>(evt.getReference())).isEmpty()).findFirst().isPresent()
				).findFirst().isPresent();
	}
}
