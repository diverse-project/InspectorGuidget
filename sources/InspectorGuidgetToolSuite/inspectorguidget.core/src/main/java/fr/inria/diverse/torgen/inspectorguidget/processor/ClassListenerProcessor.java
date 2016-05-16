package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * This processor find listener methods in the source code
 */
public class ClassListenerProcessor extends ListenerProcessor<CtClass<?>> {
	private final @NotNull Map<CtClass<?>, List<CtMethod<?>>> listenerMethods;


	public ClassListenerProcessor() {
		super();
		listenerMethods= new IdentityHashMap<>();
	}

	public @NotNull Map<CtClass<?>, List<CtMethod<?>>> getAllListenerMethods() {
		return Collections.unmodifiableMap(listenerMethods);
	}


	@Override
	public void process(final @NotNull CtClass<?> clazz) {
		LOG.log(Level.INFO, () -> "process CtClass: " + clazz);

		final BooleanProperty isAdded = new SimpleBooleanProperty(false);

		// Case SWING
		swingListenersRef.stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			if(!listenerMethods.containsKey(clazz)) {
				List<CtMethod<?>> methds = getImplementedListenerMethods(clazz, ref);
				listenerMethods.put(clazz, methds);
				swingClassObservers.forEach(l -> l.onSwingListenerClass(clazz, methds));
			}
		});

		// Case AWT
		awtListenersRef.stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			if(!listenerMethods.containsKey(clazz)) {
				List<CtMethod<?>> methds = getImplementedListenerMethods(clazz, ref);
				listenerMethods.put(clazz, methds);
				awtClassObservers.forEach(l -> l.onAWTListenerClass(clazz, methds));
			}
		});

		// Case JFX
		jfxListenersRef.stream().filter(clazz::isSubtypeOf).forEach(ref -> {
			isAdded.setValue(true);
			if(!listenerMethods.containsKey(clazz)) {
				List<CtMethod<?>> methds = getImplementedListenerMethods(clazz, ref);
				listenerMethods.put(clazz, methds);
				jfxClassObservers.forEach(l -> l.onJFXListenerClass(clazz, methds));
			}
		});

		if(!isAdded.getValue() && clazz.isSubtypeOf(eventListenerRef)) {
			LOG.log(Level.SEVERE, "Listener not supported " +
					SpoonHelper.INSTANCE.formatPosition(clazz.getPosition()) + ": " + clazz);
		}
	}


	@Override
	public boolean isToBeProcessed(final @NotNull CtClass<?> candidate) {
		return isListenerCass(candidate);
	}


	private CtTypeReference<?>[] getTypeRefFromClasses(final @NotNull Class<?>[] classes) {
		final ClassFactory facto = getFactory().Class();
		return Arrays.stream(classes).map(type -> facto.createReference(type)).toArray(CtTypeReference<?>[]::new);
	}


	/**
	 * Store each method from cl that implements interf
	 */
	private List<CtMethod<?>> getImplementedListenerMethods(final @NotNull CtClass<?> cl, final @NotNull CtTypeReference<?> interf) {
		return Arrays.stream(interf.getActualClass().getMethods()).parallel().map(interfM -> {
			CtMethod<?> m = cl.getMethod(interfM.getName(), getTypeRefFromClasses(interfM.getParameterTypes()));

			//FIXME generics in methods are not correctly managed by Spoon or Java (getClass from Class
			// does not provide any generics). So...
			if(m==null && cl.isSubtypeOf(jfxListenersRef.get(0))) {
				m = cl.getMethodsByName(interfM.getName()).get(0);
			}
			if(m==null && !cl.hasModifier(ModifierKind.ABSTRACT))
				LOG.log(Level.SEVERE, "Cannot find the implemented method " + interfM + " from the interface: " + interf + " "  +
						SpoonHelper.INSTANCE.formatPosition(cl.getPosition()));
			return m;
		}).filter(m -> m!=null).collect(Collectors.toList());
	}
}