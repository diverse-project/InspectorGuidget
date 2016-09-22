package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import org.jetbrains.annotations.NotNull;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

import java.util.Collection;
import java.util.logging.Logger;

public abstract class InspectorGuidgetProcessor <T extends CtElement> extends AbstractProcessor<T> {
	public static final @NotNull Logger LOG = Logger.getLogger("InspectorGuidget Processor");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}


	public InspectorGuidgetProcessor() {
		super();
	}

	public static boolean isASubTypeOf(final @NotNull CtTypeReference<?> candidate, final @NotNull Collection<CtTypeReference<?>> types) {
		return types.stream().filter(type -> {
			try {
				return candidate.isSubtypeOf(type);
			}catch(SpoonClassNotFoundException ex) {
//				ex.printStackTrace();
				return false;
			}
		}).findFirst().isPresent();
	}
}
