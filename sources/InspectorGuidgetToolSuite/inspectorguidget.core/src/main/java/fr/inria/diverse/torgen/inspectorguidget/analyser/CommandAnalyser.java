package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.ClassMethodCallFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.ConditionalFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.LocalVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.DirectReferenceFilter;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CommandAnalyser extends InspectorGuidetAnalyser {
	private final @NotNull ClassListenerProcessor classProc;
	private final @NotNull LambdaListenerProcessor lambdaProc;
	private final @NotNull Map<CtExecutable<?>, List<Command>> commands;

	public CommandAnalyser() {
		super(Collections.emptyList());

		commands = new IdentityHashMap<>();
		classProc=new ClassListenerProcessor();
		lambdaProc=new LambdaListenerProcessor();

		addProcessor(classProc);
		addProcessor(lambdaProc);
	}

	public @NotNull Map<CtExecutable<?>, List<Command>> getCommands() {
		return Collections.unmodifiableMap(commands);
	}

	@Override
	public void process() {
		super.process();
		final Map<CtClass<?>, List<CtMethod<?>>> methods = classProc.getAllListenerMethods();

		methods.entrySet().parallelStream().forEach(entry -> {
			if(entry.getValue().size()==1) {
				analyseSingleListenerMethod(Optional.of(entry.getKey()), entry.getValue().get(0));
			}else {
				analyseMultipleListenerMethods(entry.getKey(), entry.getValue());
			}
		});

		lambdaProc.getAllListenerLambdas().parallelStream().forEach(l -> analyseSingleListenerMethod(Optional.empty(), l));

		// Post-process to add statements (e.g. var def) used in commands bu not present in the current command (because defined before or after)
		commands.values().parallelStream().flatMap(s -> s.stream()).forEach(cmd ->
			// For each command, adding the required local variable definitions.
			cmd.getStatements().addAll(0,
				// Looking for local variable accesses in the command
				cmd.getStatements().stream().map(stat -> stat.getElements(new LocalVariableAccessFilter()).stream().
					// Selecting the local variable definitions not already contained in the command
					map(v -> v.getDeclaration()).filter(v -> !cmd.getStatements().contains(v)).
					collect(Collectors.toList())).flatMap(s -> s.stream()).map(elt -> (CtStatement)elt).collect(Collectors.toList())));
	}


	private void extractCommandsFromConditionalStatements(final @NotNull CtElement condStat, final @NotNull CtExecutable<?> listenerMethod) {
		List<Command> cmds = commands.get(listenerMethod);

		if(cmds==null) {
			cmds = new ArrayList<>();
			commands.put(listenerMethod, cmds);
		}

		if(condStat instanceof CtIf) {
			extractCommandsFromIf((CtIf) condStat, cmds);
			return;
		}

		if(condStat instanceof CtSwitch<?>) {
			extractCommandsFromSwitch((CtSwitch<?>) condStat, cmds);
			return;
		}

		LOG.log(Level.SEVERE, "Unsupported conditional blocks: " + condStat);
	}


	private void extractCommandsFromSwitch(final @NotNull CtSwitch<?> switchStat, final @NotNull List<Command> cmds) {
		cmds.addAll(switchStat.getCases().stream().
		// Ignoring the case statements that are empty
			filter(cas -> !cas.getStatements().isEmpty() && (cas.getStatements().size() > 1 || !SpoonHelper.INSTANCE.isReturnBreakStatement(cas.getStatements().get(cas.getStatements().size() - 1)))).
			map(cas -> {
				// Creating the body of the command.
				final List<CtStatement> stats = new ArrayList<>(cas.getStatements());

				// Removing the last 'return' or 'break' statement from the command.
				if(SpoonHelper.INSTANCE.isReturnBreakStatement(stats.get(stats.size() - 1))) {
					stats.remove(stats.size() - 1);
				}

				final List<CommandConditionEntry> conds = getsuperConditionalStatements(switchStat);
				conds.add(0, new CommandConditionEntry(SpoonHelper.INSTANCE.createEqExpressionFromSwitchCase(switchStat, cas)));
				//For each case, a condition is created using the case value.
				return new Command(stats, conds);
			}).collect(Collectors.toList()));
	}


	private void extractCommandsFromIf(final @NotNull CtIf ifStat, final @NotNull List<Command> cmds) {
		final CtStatement elseStat =  ifStat.getElseStatement();
		final CtStatement thenStat = ifStat.getThenStatement();
		List<CtStatement> stats = new ArrayList<>();

		if(thenStat instanceof CtStatementList)
			stats.addAll(((CtStatementList)thenStat).getStatements());
		else
			stats.add(thenStat);

		if(!stats.isEmpty()) {
			if(stats.get(stats.size() - 1) instanceof CtReturn<?>)
				stats.remove(stats.size() - 1);

			if(!stats.isEmpty()) {
				final List<CommandConditionEntry> conds = getsuperConditionalStatements(ifStat);
				conds.add(0, new CommandConditionEntry(ifStat.getCondition()));
				cmds.add(new Command(stats, conds));
			}
		}

		if(elseStat!=null) {
			//TODO create a command if it does not contain any other GUI conditional statement
			// For the else block, creating a negation of the condition.
			stats = new ArrayList<>();

			if(elseStat instanceof CtStatementList)
				stats.addAll(((CtStatementList)elseStat).getStatements());
			else
				stats.add(elseStat);

			if(!stats.isEmpty()) {
				if(stats.get(stats.size() - 1) instanceof CtReturn<?>)
					stats.remove(stats.size() - 1);

				if(!stats.isEmpty()) {
					final List<CommandConditionEntry> conds = getsuperConditionalStatements(ifStat);
					conds.add(0, new CommandConditionEntry(elseStat, SpoonHelper.INSTANCE.negBoolExpression(ifStat.getCondition())));
					cmds.add(new Command(stats, conds));
				}
			}
		}
	}


	/**
	 * Explores the parent of the given statement up to the method definition to identify all the conditional statements that
	 * lead to the given one.
	 * @param condStat The conditional statement to use.
	 * @return The list of all the conditional statements.
	 */
	private List<CommandConditionEntry> getsuperConditionalStatements(final @NotNull CtElement condStat) {
		CtElement currElt = condStat;
		CtElement parent = currElt.getParent();
		List<CommandConditionEntry> conds = new ArrayList<>();

		// Exploring the parents to identify the conditional statements
		while(parent!=null) {
			if(parent instanceof CtIf) {
				CtIf ctif = (CtIf) parent;
				CtExpression<Boolean> condition = ctif.getCondition();

				// Identifying the block of the if used and adding a condition.
				if(ctif.getThenStatement()==currElt) {
					conds.add(new CommandConditionEntry(condition));
				}else if(ctif.getElseStatement()==currElt) {
					conds.add(new CommandConditionEntry(condition, SpoonHelper.INSTANCE.negBoolExpression(condition)));
				}else {
					LOG.log(Level.SEVERE, "Cannot find the origin of the statement in the if statement " +
							SpoonHelper.INSTANCE.formatPosition(parent.getPosition()) +  " + : " + parent);
				}
			}else if(parent instanceof CtSwitch<?>) {
				final CtElement elt = parent;
				CtSwitch<?> ctswitch = (CtSwitch<?>) parent;
				// Identifying the case statement used and creating a condition.
				// The use of orElse(null) is mandatory here (berk!) to avoid a strange compilation bug with generics and casting.
				CtCase<?> caz = ctswitch.getCases().stream().filter(cas -> cas == elt).findFirst().orElse(null);

				if(caz==null) {
					LOG.log(Level.SEVERE, "Cannot find the origin of the statement in the switch statement " +
							SpoonHelper.INSTANCE.formatPosition(parent.getPosition()) +  " + : " + parent);
				}else {
					conds.add(new CommandConditionEntry(caz.getCaseExpression(),
														SpoonHelper.INSTANCE.createEqExpressionFromSwitchCase(ctswitch, caz)));
				}
			}

			currElt = parent;
			parent = parent.getParent();
		}

		return conds;
	}


	private void analyseSingleListenerMethod(final @NotNull  Optional<CtClass<?>> listenerClass,
											 final @NotNull CtExecutable<?> listenerMethod) {
		if(listenerMethod.getBody()==null || listenerMethod.getBody().getStatements().isEmpty()) {
			// Empty so no command
			synchronized(commands) { commands.put(listenerMethod, Collections.emptyList()); }
		}else {
			final List<CtElement> conds = getConditionalStatements(listenerMethod, listenerClass);

			if(conds.isEmpty()) {
				// when no conditional, the content of the method forms a command.
				synchronized(commands) {
					commands.put(listenerMethod, Collections.singletonList(
							new Command(listenerMethod.getBody().getStatements(), Collections.emptyList())));
				}
			}else {
				// For each conditional statements found in the listener method or in its dispatched methods,
				// a command is extracted.
				conds.forEach(cond -> extractCommandsFromConditionalStatements(cond, listenerMethod));
			}
		}
	}


	private @NotNull List<CtElement> getConditionalStatements(final @Nullable CtExecutable<?> exec,
																final @NotNull Optional<CtClass<?>> listenerClass) {
		if(exec==null || exec.getBody()==null)
			return Collections.emptyList();

		final List<CtElement> conds = new ArrayList<>();

		if(listenerClass.isPresent()) { // Searching for dispatched methods is not performed on lambdas.
			conds.addAll(
					// Getting all the methods called in the current method that use a parameter of this last.
					// The goal is to identify the dispatched methods, recursively.
					exec.getElements(new ClassMethodCallFilter(exec.getParameters(), listenerClass.get())).stream().
					// For each dispatched methods, looking for conditional statements.
					map(dispatchM -> getConditionalStatements(dispatchM.getExecutable().getDeclaration(), listenerClass)).
					flatMap(c -> c.stream()).collect(Collectors.toList()));
		}

		final List<CtParameterReference<?>> guiParams = exec.getParameters().stream().map(param -> param.getReference()).collect(Collectors.toList());

		// Getting all the conditional statements
		conds.addAll(exec.getBody().getElements(new ConditionalFilter()).stream().
						// Keeping those making use of a GUI parameter.
						filter(cond -> conditionalUsesGUIParam(cond, guiParams)).
						collect(Collectors.toList()));

		// Removing the GUI conditional statements that contain other GUI conditional statements.
		conds.removeAll(conds.stream().filter(cond -> {
			List<CtStatement> elements = cond.getElements(new ConditionalFilter());
			// By default 'elements' contains the condition 'cond'.
			return elements.size()>1 &&
					// Ignoring 'cond'
					elements.stream().filter(cond2 -> cond!=cond2).
					anyMatch(cond2 -> conds.contains(cond2));
		}).collect(Collectors.toList()));

		return conds;
	}


	private boolean conditionalUsesGUIParam(final CtStatement stat, final List<CtParameterReference<?>> guiParams) {
		CtExpression<?> condition;

		if(stat instanceof CtIf)
			condition = ((CtIf)stat).getCondition();
		else if(stat instanceof CtSwitch<?>)
			condition = ((CtSwitch<?>)stat).getSelector();
		else condition = null;

		if(condition==null)
			return false;

		return elementUsesGUIParam(condition, guiParams);
	}


	private boolean elementUsesGUIParam(final CtElement elt, final List<CtParameterReference<?>> guiParams) {
		// Check whether a GUI parameter is directly used in the statement.
		if(guiParams.stream().filter(param -> !elt.getReferences(new DirectReferenceFilter<>(param)).isEmpty()).findFirst().isPresent())
			return true;

		// Otherwise, looking for local variables that use a GUI parameter.
		return elt.getElements(new LocalVariableAccessFilter()).stream().
				filter(var -> elementUsesGUIParam(var.getDeclaration(), guiParams)).
				findFirst().isPresent();
	}


	private void analyseMultipleListenerMethods(final @NotNull CtClass<?> listenerClass, final @NotNull List<CtMethod<?>> listenerMethods) {
		final List<CtMethod<?>> nonEmptyM=listenerMethods.stream().
				filter(l -> l.getBody() != null && !l.getBody().getStatements().isEmpty()).collect(Collectors.toList());

		switch(nonEmptyM.size()) {
			case 0:
				synchronized(commands) { listenerMethods.forEach(l -> commands.put(l, Collections.emptyList())); }
				break;
			case 1:
				analyseSingleListenerMethod(Optional.of(listenerClass), nonEmptyM.get(0));
				break;
			default:
				//TODO
				break;
		}
	}
}
