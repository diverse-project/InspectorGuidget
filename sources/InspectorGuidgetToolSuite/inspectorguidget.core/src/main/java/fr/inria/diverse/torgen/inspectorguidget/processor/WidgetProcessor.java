package fr.inria.diverse.torgen.inspectorguidget.processor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Detects declaration of widgets.
 */
public class WidgetProcessor extends InspectorGuidgetProcessor<CtTypeReference<?>> {
	protected Collection<CtTypeReference<?>> controlType;
	protected Map<CtField<?>, CtField<?>> fields;
	protected CtTypeReference<?> collectionType;

	@Override
	public void init() {
		if(LOG.isLoggable(Level.ALL)) LOG.log(Level.INFO, "init processor " + getClass().getSimpleName());

		fields = new IdentityHashMap<>();
		controlType = new ArrayList<>();
		collectionType = getFactory().Type().createReference(Collection.class);

		controlType.add(getFactory().Type().createReference(javafx.scene.Node.class));
		controlType.add(getFactory().Type().createReference(javafx.scene.control.MenuItem.class));
		controlType.add(getFactory().Type().createReference(javafx.scene.control.Dialog.class));
		controlType.add(getFactory().Type().createReference(javafx.stage.Window.class));
		controlType.add(getFactory().Type().createReference(java.awt.Component.class));
	}

	@Override
	public boolean isToBeProcessed(CtTypeReference<?> type) {
		return isASubTypeOf(type, controlType) || type.isSubtypeOf(collectionType) &&
				type.getActualTypeArguments().stream().filter(t -> isASubTypeOf(t, controlType)).findFirst().isPresent();
	}

	@Override
	public void process(final CtTypeReference<?> element) {
		CtElement parent = element.getParent();

		if(parent instanceof CtField<?> && !fields.containsKey(parent)) {
			final CtField<?> field = (CtField<?>) parent;
			fields.put(field, field);
			jfxClassObservers.forEach(o -> o.onJFXWidgetAttribute(field));
		}
	}
}
