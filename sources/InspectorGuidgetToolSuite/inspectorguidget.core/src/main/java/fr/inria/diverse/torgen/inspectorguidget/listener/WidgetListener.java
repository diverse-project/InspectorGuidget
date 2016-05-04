package fr.inria.diverse.torgen.inspectorguidget.listener;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

public interface WidgetListener {
	void onWidgetAttribute(CtField<?> widget, CtTypeReference<?> element);

	void onWidgetCreatedForContainer(CtInvocation<?> widgetInvoc, CtTypeReference<?> element);
}
