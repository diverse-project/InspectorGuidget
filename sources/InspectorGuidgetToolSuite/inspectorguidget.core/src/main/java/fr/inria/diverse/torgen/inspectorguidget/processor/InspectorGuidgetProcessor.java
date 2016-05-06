package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import fr.inria.diverse.torgen.inspectorguidget.listener.AWTListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.listener.SwingListenerClass;
import org.jetbrains.annotations.NotNull;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public abstract class InspectorGuidgetProcessor <T extends CtElement> extends AbstractProcessor<T> {
	protected static final @NotNull Logger LOG = Logger.getLogger("InspectorGuidget Processor");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}

	protected final @NotNull Set<AWTListenerClass> awtClassObservers;
	protected final @NotNull Set<SwingListenerClass> swingClassObservers;
	protected final @NotNull Set<JFXListenerClass> jfxClassObservers;


	public InspectorGuidgetProcessor() {
		super();
		awtClassObservers= new HashSet<>();
		swingClassObservers= new HashSet<>();
		jfxClassObservers= new HashSet<>();
	}

	public void addAWTClassListener(final @NotNull AWTListenerClass lis) {
		awtClassObservers.add(lis);
	}

	public void addSwingClassListener(final @NotNull SwingListenerClass lis) {
		swingClassObservers.add(lis);
	}

	public void addJFXClassListener(final @NotNull JFXListenerClass lis) {
		jfxClassObservers.add(lis);
	}

	public static boolean isASubTypeOf(final @NotNull CtTypeReference<?> candidate, final @NotNull Collection<CtTypeReference<?>> types) {
		return types.stream().filter(candidate::isSubtypeOf).findFirst().isPresent();
	}
}
