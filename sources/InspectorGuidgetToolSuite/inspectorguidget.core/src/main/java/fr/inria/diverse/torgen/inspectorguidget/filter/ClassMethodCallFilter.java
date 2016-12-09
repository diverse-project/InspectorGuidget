package fr.inria.diverse.torgen.inspectorguidget.filter;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.VariableAccessFilter;

public class ClassMethodCallFilter extends AbstractFilter<CtInvocation<?>> {
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
		return SpoonHelper.INSTANCE.hasMethod(listenerClass, element.getExecutable().getDeclaration()) &&
			// Is there any parameters of the method that is a GUI parameter
			element.getArguments().stream().anyMatch(arg -> events.stream().anyMatch(evt ->
				!arg.getElements(new VariableAccessFilter<>(evt.getReference())).isEmpty()) == withGUIParams) == withGUIParams;
	}
}
