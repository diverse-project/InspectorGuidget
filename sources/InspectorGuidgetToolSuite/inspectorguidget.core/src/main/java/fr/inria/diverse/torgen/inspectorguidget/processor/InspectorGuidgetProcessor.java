package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import fr.inria.diverse.torgen.inspectorguidget.listener.AWTListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.listener.JFXListenerClass;
import fr.inria.diverse.torgen.inspectorguidget.listener.SwingListenerClass;
import org.eclipse.jdt.annotation.NonNull;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public abstract class InspectorGuidgetProcessor <T extends CtElement> extends AbstractProcessor<T> {
	protected static final Logger LOG = Logger.getLogger("InspectorGuidget Processor");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}

	protected final Set<AWTListenerClass> awtClassObservers;
	protected final Set<SwingListenerClass> swingClassObservers;
	protected final Set<JFXListenerClass> jfxClassObservers;


	public InspectorGuidgetProcessor() {
		super();
		awtClassObservers= new HashSet<>();
		swingClassObservers= new HashSet<>();
		jfxClassObservers= new HashSet<>();
	}

	public void addAWTClassListener(final @NonNull AWTListenerClass lis) {
		awtClassObservers.add(lis);
	}

	public void addSwingClassListener(final @NonNull SwingListenerClass lis) {
		swingClassObservers.add(lis);
	}

	public void addJFXClassListener(final @NonNull JFXListenerClass lis) {
		jfxClassObservers.add(lis);
	}

	public static boolean isASubTypeOf(final CtTypeReference<?> candidate, final Collection<CtTypeReference<?>> types) {
		return types.stream().filter(candidate::isSubtypeOf).findFirst().isPresent();
	}
}
