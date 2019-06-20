package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.reference.CtTypeReference;

/**
 * This processor find listener methods in the source code
 */
public class ClassListenerProcessor extends InspectorGuidgetProcessor<CtClass<?>> {
	private final @NotNull Map<CtClass<?>, Set<CtMethod<?>>> listenerMethods;


	public ClassListenerProcessor() {
		super();
		listenerMethods = new IdentityHashMap<>();
	}

	public @NotNull Map<CtClass<?>, Set<CtMethod<?>>> getAllListenerMethods() {
		return Collections.unmodifiableMap(listenerMethods);
	}


	@Override
	public void process(final @NotNull CtClass<?> clazz) {
		LOG.log(Level.INFO, () -> "process CtClass: " + clazz);

		final BooleanProperty isAdded = new SimpleBooleanProperty(false);

		// Case SWING
		WidgetHelper.INSTANCE.getSwingListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		// Case AWT
		WidgetHelper.INSTANCE.getAWTListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		// Case JFX
		WidgetHelper.INSTANCE.getJFXListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		// Case SWT
		WidgetHelper.INSTANCE.getSWTListenersRef(getFactory()).stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			addListenerMethodsFrom(ref, clazz);
		});

		if(!isAdded.getValue() && WidgetHelper.INSTANCE.isListenerClass(clazz, getFactory(), null)) {
			LOG.log(Level.WARNING, "Listener not supported " + SpoonHelper.INSTANCE.formatPosition(clazz.getPosition()) + ": " + clazz.getQualifiedName());
		}
	}


	private void addListenerMethodsFrom(final @NotNull CtTypeReference<?> ref, final @NotNull CtClass<?> clazz) {
		final Set<CtMethod<?>> methods = getImplementedListenerMethods(clazz, ref);
		final Set<CtMethod<?>> savedMethods = listenerMethods.get(clazz);

		if(savedMethods != null) {
			methods.addAll(savedMethods);
		}

		listenerMethods.put(clazz, methods);
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtClass<?> candidate) {
		return WidgetHelper.INSTANCE.isListenerClass(candidate, getFactory(), null);
	}


	private CtTypeReference<?>[] getTypeRefFromClasses(final @NotNull Class<?>[] classes) {
		final ClassFactory facto = getFactory().Class();
		return Arrays.stream(classes).map(type -> facto.createReference(type)).toArray(CtTypeReference<?>[]::new);
	}


	/**
	 * Store each method from cl that implements interf
	 */
	private Set<CtMethod<?>> getImplementedListenerMethods(final @NotNull CtClass<?> cl, final @NotNull CtTypeReference<?> interf) {
		return interf.getTypeDeclaration().getMethods()
			.parallelStream()
			.map(interfM -> {
				CtMethod<?> m = cl.getMethod(interfM.getSimpleName(), getTypeRefFromClasses(interfM.getParameterTypes()));

				//FIXME generics in methods are not correctly managed by Spoon or Java (getClass from Class
				// does not provide any generics). So...
				if(m == null && cl.isSubtypeOf(WidgetHelper.INSTANCE.getJFXListenersRef(getFactory()).get(0))) {
					m = cl.getMethodsByName(interfM.getSimpleName()).get(0);
				}
				return m;
			})
			.filter(m -> m != null)
			.collect(Collectors.toSet());
	}
}