package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import java.util.Collection;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public abstract class InspectorGuidgetProcessor <T extends CtElement> extends AbstractProcessor<T> {
	public static final @NotNull Logger LOG = Logger.getLogger("InspectorGuidget Processor");

	static {
		LOG.setLevel(LoggingHelper.INSTANCE.loggingLevel);
	}


	public InspectorGuidgetProcessor() {
		super();
	}

	public static boolean isASubTypeOf(final @Nullable CtTypeReference<?> candidate, final @NotNull Collection<CtTypeReference<?>> types) {
		return candidate!=null && types.stream().anyMatch(type -> {
			try {
				return candidate.isSubtypeOf(type);
			}catch(SpoonClassNotFoundException ex) {
//				ex.printStackTrace();
				return false;
			}
		});
	}
}
