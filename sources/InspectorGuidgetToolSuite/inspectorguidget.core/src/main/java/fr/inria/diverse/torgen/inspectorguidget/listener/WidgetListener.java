package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public interface WidgetListener {
	void onWidgetAttribute(CtVariable<?> widget, List<CtVariableAccess<?>> usages, CtTypeReference<?> element);

	void onWidgetCreatedInExistingVar(CtAssignment<?,?> assig, List<CtVariableAccess<?>> usages, CtTypeReference<?> element);

	void onWidgetCreatedForContainer(CtInvocation<?> widgetInvoc, CtTypeReference<?> element);
}
