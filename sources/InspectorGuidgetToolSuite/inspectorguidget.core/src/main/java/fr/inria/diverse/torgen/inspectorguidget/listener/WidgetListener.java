package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public interface WidgetListener {
	void onWidgetAttribute(CtField<?> widget, List<CtVariableAccess<?>> usages, CtTypeReference<?> element);

	void onWidgetCreatedForContainer(CtInvocation<?> widgetInvoc, CtTypeReference<?> element);
}
