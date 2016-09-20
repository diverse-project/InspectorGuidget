package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtClass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ActionProcessor extends InspectorGuidgetProcessor<CtClass<?>> {
	private final @NotNull Set<CtClass<?>> actions;

	public ActionProcessor() {
		super();
		actions = new HashSet<>();
	}

	public @NotNull Set<CtClass<?>> getActions() {
		return Collections.unmodifiableSet(actions);
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtClass<?> candidate) {
		return candidate.isSubtypeOf(WidgetHelper.INSTANCE.getActionRef(candidate.getFactory()));
	}


	@Override
	public void process(final @NotNull CtClass<?> clazz) {
		actions.add(clazz);
	}
}
