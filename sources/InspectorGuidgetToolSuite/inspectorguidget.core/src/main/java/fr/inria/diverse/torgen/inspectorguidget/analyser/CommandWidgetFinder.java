package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.filter.TypeRefFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtVariableReference;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An analyser to find the widget(s) that produce(s) a given command.
 */
public class CommandWidgetFinder {
	private final @NotNull List<Command> cmds;
	private final @NotNull Map<Command, WidgetFinderEntry> results;

	/**
	 * Craetes the analyser.
	 * @param commands the set of commands to analyse.
	 */
	public CommandWidgetFinder(final @NotNull List<Command> commands) {
		super();
		cmds = commands;
		results = new HashMap<>();
	}

	/**
	 * Executes the analysis.
	 */
	public void process() {
		cmds.parallelStream().forEach(cmd -> process(cmd));
	}

	private void process(final @NotNull Command cmd) {
		final WidgetFinderEntry entry = new WidgetFinderEntry();

		synchronized(results) {
			results.put(cmd, entry);
		}

		getAssociatedListenerVariable(cmd).ifPresent(varref -> entry.setRegisteredWidgets(Collections.singletonList(varref)));
		entry.setWidgetsUsedInConditions(getVarWidgetInListener(cmd));
	}


	/**
	 * Identifies the widgets used the conditions of the given command.
	 * @param cmd The comand to analyse
	 * @return The list of the references to the widgets used in the conditions.
	 */
	private List<CtVariableReference<?>> getVarWidgetInListener(final @NotNull Command cmd) {
		final TypeRefFilter filter = new TypeRefFilter(WidgetHelper.INSTANCE.getWidgetTypes(cmd.getExecutable().getFactory()));

		return cmd.getConditions().stream().map(cond -> cond.realStatmt.getElements(filter).stream().
											map(w -> w.getParent(CtVariableReference.class))).
											flatMap(s -> s).collect(Collectors.toList());
	}


	/**
	 * Identifies the widget on which the listener is added.
	 * @param cmd The command to analyse.
	 * @return The reference to the widget or nothing.
	 */
	private Optional<CtVariableReference<?>> getAssociatedListenerVariable(final @NotNull Command cmd) {
		final CtExecutable<?> listenerMethod = cmd.getExecutable();
		final CtInvocation<?> invok = listenerMethod.getParent(CtInvocation.class);

		if(invok==null)
			return Optional.empty();

		final CtExpression<?> target = invok.getTarget();

		if(target instanceof CtFieldRead<?>) {
			final CtFieldRead<?> fieldRead = (CtFieldRead<?>) target;

			if(WidgetHelper.INSTANCE.isTypeRefAWidget(fieldRead.getType())) {
				return Optional.of(fieldRead.getVariable());
			}
		}else if(target instanceof CtVariableRead<?>) {
			final CtVariableRead<?> variableRead = (CtVariableRead<?>) target;

			if(WidgetHelper.INSTANCE.isTypeRefAWidget(variableRead.getType())) {
				return Optional.of(variableRead.getVariable());
			}
		}else {
			System.out.println("INVOCATION TARGET TYPE NOT SUPPORTED: " + target.getClass());
		}

		return Optional.empty();
	}


	/**
	 * @return A unmodifiable map of the results of the process.
	 */
	public @NotNull Map<Command, WidgetFinderEntry> getResults() {
		return Collections.unmodifiableMap(results);
	}


	public static final class WidgetFinderEntry {
		private List<CtVariableReference<?>> registeredWidgets;
		private List<CtVariableReference<?>> widgetsUsedInConditions;

		private WidgetFinderEntry() {
			super();
			registeredWidgets = Collections.emptyList();
			widgetsUsedInConditions = Collections.emptyList();
		}

		public List<CtVariableReference<?>> getRegisteredWidgets() {
			return Collections.unmodifiableList(registeredWidgets);
		}

		public List<CtVariableReference<?>> getWidgetsUsedInConditions() {
			return Collections.unmodifiableList(widgetsUsedInConditions);
		}

		private void setRegisteredWidgets(final @NotNull List<CtVariableReference<?>> registeredWidgets) {
			this.registeredWidgets = registeredWidgets;
		}

		private void setWidgetsUsedInConditions(final @NotNull List<CtVariableReference<?>> widgetsUsedInConditions) {
			this.widgetsUsedInConditions = widgetsUsedInConditions;
		}

		public long getNbDistinctWidgets() {
			return Stream.concat(registeredWidgets.stream(), widgetsUsedInConditions.stream()).distinct().count();
		}
	}
}
