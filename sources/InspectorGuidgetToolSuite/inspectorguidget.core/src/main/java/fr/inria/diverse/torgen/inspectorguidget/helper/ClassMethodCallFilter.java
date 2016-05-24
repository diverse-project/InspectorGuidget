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
	private final boolean withGUIParams;

	public ClassMethodCallFilter(final @NotNull List<CtParameter<?>> guiEvents, final @NotNull CtClass<?> clazz, final boolean guiParams) {
		super(CtElement.class);
		events = guiEvents;
		listenerClass = clazz;
		withGUIParams = guiParams;
	}

	@Override
	public boolean matches(final CtInvocation<?> element) {
				// Does the invocated method part of the class
		return listenerClass.getAllMethods().contains(element.getExecutable().getDeclaration()) &&
				// Is there any parameters of the method that is a GUI parameter
				element.getArguments().stream().filter(arg ->
					events.stream().filter(evt ->
						!arg.getElements(new VariableAccessFilter<>(evt.getReference())).isEmpty()).findFirst().isPresent()==withGUIParams
				).findFirst().isPresent()==withGUIParams;
	}
}
