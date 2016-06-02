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

/**
 * An analyser to find the widget(s) that produce(s) a given command.
 */
public class CommandWidgetFinder {
	private final @NotNull List<Command> cmds;
	private final @NotNull Map<Command, List<CtVariableReference<?>>> results;

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
		getAssociatedListenerVariable(cmd).ifPresent(varref -> {
			synchronized(results) {
				results.put(cmd, Collections.singletonList(varref));
			}
		});

		List<CtVariableReference<?>> widgets = getVarWidgetInListener(cmd);

		synchronized(results) {
			if(!widgets.isEmpty()) {
				List<CtVariableReference<?>> vars = results.get(cmd);
				if(vars != null) {
					widgets.addAll(vars);
				}
				results.put(cmd, widgets);
			}
		}
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
											flatMap(s -> s).collect(Collectors.<CtVariableReference<?>>toList());
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
	public @NotNull Map<Command, List<CtVariableReference<?>>> getResults() {
		return Collections.unmodifiableMap(results);
	}
}
