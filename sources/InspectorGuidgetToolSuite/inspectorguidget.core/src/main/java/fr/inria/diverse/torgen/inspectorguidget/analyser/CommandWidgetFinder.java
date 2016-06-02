package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtVariableReference;

import java.util.*;

/**
 * An analyser to find the widget(s) that produce(s) a given command.
 */
public class CommandWidgetFinder {
	private final @NotNull List<Command> cmds;
	private final @NotNull Map<CtVariableReference<?>, Command> results;

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
//		System.out.println("NB CMDS: " + cmds.size());
		cmds.parallelStream().forEach(cmd -> process(cmd));
	}

	private void process(final @NotNull Command cmd) {
		getAssociatedListenerVariable(cmd).ifPresent(varref -> {
			results.put(varref, cmd);
		});

//		final TypeRefFilter filter = new TypeRefFilter(WidgetHelper.INSTANCE.getWidgetTypes(cmd.getExecutable().getFactory()));
//
//		cmd.getConditions().stream().forEach(cond -> {
//			List<CtTypeReference<?>> widgets = cond.realStatmt.getElements(filter);
//			switch(widgets.size()) {
//				case 0:
//					break;
//				case 1:
//					break;
//				default:
//					break;
//			}
//			System.out.println(widgets + " " + cmd.getConditions());
//		});
	}


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
	public @NotNull Map<CtVariableReference<?>, Command> getResults() {
		return Collections.unmodifiableMap(results);
	}
}
