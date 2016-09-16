package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.filter.*;
import fr.inria.diverse.torgen.inspectorguidget.helper.*;
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
		classProc = new ClassListenerProcessor();
		lambdaProc = new LambdaListenerProcessor();

		addProcessor(classProc);
		addProcessor(lambdaProc);
	}

	public @NotNull Map<CtExecutable<?>, List<Command>> getCommands() {
		synchronized(commands) { return Collections.unmodifiableMap(commands); }
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

		// Post-process to add statements (e.g. var def) used in commands but not present in the current command (because defined before or after)
		synchronized(commands) {
			commands.entrySet().forEach(entry -> entry.getValue().forEach(cmd -> {
					cmd.extractLocalDispatchCallWithoutGUIParam();
					// For each command, adding the required local variable definitions.
					cmd.addAllStatements(0,
						// Looking for local variable accesses in the command
						cmd.getAllStatmts().stream().map(stat -> stat.getElements(new LocalVariableAccessFilter()).stream().
							// Selecting the local variable definitions not already contained in the command
								map(v -> v.getDeclaration()).filter(v -> !cmd.getAllStatmts().stream().filter(s -> s==v).findFirst().isPresent()).
								collect(Collectors.toList())).flatMap(s -> s.stream()).
							// For each var def, creating a command statement entry that will be added to the list of entries of the command.
								map(elt -> new CommandStatmtEntry(false, Collections.singletonList((CtCodeElement)elt))).collect(Collectors.toList()));

					inferLocalVarUsages(cmd, entry.getValue(), entry.getKey());
				}
			));
		}
	}


	/**
	 * Local variables used in a command must be considered when extracting a command: a backward static slicing is done here to
	 * identify all the statements that use these local variables before each use of the variables in the command.
	 * @param cmd The command to analyse.
	 * @param cmds The set of commands of the listener where cmd comes from.
	 * @param listener The listener method that contains the commands.
	 */
	private void inferLocalVarUsages(final @NotNull Command cmd, final @NotNull List<Command> cmds, final @NotNull CtExecutable<?> listener) {
		// Adding all the required elements.
		cmd.addAllStatements(
			inferLocalVarUsagesRecursive(
				cmd.getStatements().stream().map(stat -> stat.getStatmts().stream()).flatMap(s -> s).collect(Collectors.toSet()),
				new HashSet<>(), listener
			).parallelStream().filter(exp -> !isPartOfMainCommandBlockOrCondition(exp, cmd, cmds)).
			map(exp -> new CommandStatmtEntry(false, Collections.singletonList(exp))).
			collect(Collectors.toList())
		);
	}


	/**
	 * Recusion method for inferLocalVarUsages.
	 * @param stats The set of statements to analyse.
	 * @param analysedStats The set of statements already analysed.
	 * @param listener The listener method that contains the commands.
	 * @return The set of statements that the 'stats' statements depend on.
	 */
	private Set<CtElement> inferLocalVarUsagesRecursive(final @NotNull Set<CtElement> stats, final @NotNull Set<CtElement> analysedStats,
														final @NotNull CtExecutable<?> listener) {
		// For each statement of the command.
		Set<CtElement> inferred = stats.parallelStream().map(elt ->
			// Getting the local var used in the statement.
			elt.getElements(new LocalVariableAccessFilter()).stream().
				// Only the local variables defined in the listener must be considered.
				filter(var -> var.getDeclaration().getParent(CtExecutable.class)==listener).
				map(var ->
					// Finding the uses of the local var in the executable
					var.getDeclaration().getParent(CtExecutable.class).getBody().getElements(new MyVariableAccessFilter(var.getDeclaration())).stream().
						// Considering the var accesses that operate before the statement only.
						filter(varacesss -> varacesss.getPosition().getLine() <= elt.getPosition().getLine()).
						map(varaccess -> {
							// Getting all the super conditional statements.
							List<CtElement> exps = SpoonHelper.INSTANCE.getSuperConditionalExpressions(varaccess);
							// Getting the main expression of the var access (or the var access itself).
							CtExpression<?> parent = varaccess.getParent(CtExpression.class);
							exps.add(parent==null ? varaccess : parent);
							exps.add(var.getDeclaration());
							return exps;
						}).flatMap(s -> s.stream())
				).flatMap(s -> s)).flatMap(s -> s).distinct().collect(Collectors.toSet());

		if(!inferred.isEmpty()) {
			analysedStats.addAll(stats);
			inferred.addAll(inferLocalVarUsagesRecursive(inferred.parallelStream().
					filter(exp -> !analysedStats.contains(exp)).collect(Collectors.toSet()), analysedStats, listener));
		}

		return inferred;
	}


	/**
	 * Checks whether the given element 'elt' is contained in the main block or in a condition statement of a command.
	 * @param elt The element to test.
	 * @param currCmd The command from which the element comes from.
	 * @param cmds The set of commands to look into.
	 * @return True if the given element is contained in the main block or in a condition statement of a command.
	 */
	private boolean isPartOfMainCommandBlockOrCondition(final @NotNull CtElement elt, final @NotNull Command currCmd, final @NotNull List<Command> cmds) {
		final FindElementFilter filter = new FindElementFilter(elt, true);

		// First, check the main blocks
		boolean ok = cmds.parallelStream().filter(cmd -> cmd!=currCmd). // Ignoring the current command.
				map(cmd -> cmd.getMainStatmtEntry()). // Getting the main blocks
				// Searching for the given element in the statements of the main blocks.
				filter(main -> main.isPresent() && main.get().getStatmts().stream().filter(stat -> !stat.getElements(filter).isEmpty()).findFirst().isPresent()).
				findFirst().isPresent();

		// If not found, check the conditions.
		if(!ok) {
			ok = cmds.parallelStream().filter(cmd -> cmd!=currCmd).// Ignoring the current command.
				// Searching for the given element in the conditions.
				filter(cmd -> cmd.getConditions().stream().filter(cond -> !cond.realStatmt.getElements(filter).isEmpty()).findFirst().isPresent()).
				findFirst().isPresent();
		}

		return ok;
	}


	private void extractCommandsFromConditionalStatements(final @NotNull CtElement condStat, final @NotNull CtExecutable<?> listenerMethod,
														  final @NotNull List<CtElement> conds) {
		List<Command> cmds;
		synchronized(commands) {
			cmds = commands.get(listenerMethod);

			if(cmds == null) {
				cmds = new ArrayList<>();
				commands.put(listenerMethod, cmds);
			}
		}

		if(condStat instanceof CtIf) {
			extractCommandsFromIf((CtIf) condStat, cmds, listenerMethod, conds);
			return;
		}

		if(condStat instanceof CtSwitch<?>) {
			extractCommandsFromSwitch((CtSwitch<?>) condStat, cmds, listenerMethod);
			return;
		}

		LOG.log(Level.SEVERE, "Unsupported conditional blocks: " + condStat);
	}


	private void extractCommandsFromSwitch(final @NotNull CtSwitch<?> switchStat, final @NotNull List<Command> cmds,
										   final @NotNull CtExecutable<?> exec) {
		cmds.addAll(switchStat.getCases().stream().
		// Ignoring the case statements that are empty
			filter(cas -> !cas.getStatements().isEmpty() && (cas.getStatements().size() > 1 || !SpoonHelper.INSTANCE.isReturnBreakStatement(cas.getStatements().get(cas.getStatements().size() - 1)))).
			map(cas -> {
				// Creating the body of the command.
				final List<CtElement> stats = new ArrayList<>(cas.getStatements());

//				// Removing the last 'return' or 'break' statement from the command.
//				if(SpoonHelper.INSTANCE.isReturnBreakStatement(stats.get(stats.size() - 1))) {
//					stats.remove(stats.size() - 1);
//				}

				final List<CommandConditionEntry> conds = getsuperConditionalStatements(switchStat);
				conds.add(0, new CommandConditionEntry(cas, SpoonHelper.INSTANCE.createEqExpressionFromSwitchCase(switchStat, cas)));
				//For each case, a condition is created using the case value.
				return new Command(new CommandStatmtEntry(true, stats), conds, exec);
			}).collect(Collectors.toList()));
	}


	private void extractCommandsFromIf(final @NotNull CtIf ifStat, final @NotNull List<Command> cmds, final @NotNull CtExecutable<?> exec,
									   final @NotNull List<CtElement> otherConds) {
		final CtStatement elseStat =  ifStat.getElseStatement();
		final CtStatement thenStat = ifStat.getThenStatement();
		List<CtElement> stats = new ArrayList<>();

		if(thenStat instanceof CtStatementList)
			stats.addAll(((CtStatementList)thenStat).getStatements());
		else
			stats.add(thenStat);

		if(stats.size()>1 || !stats.isEmpty() && !SpoonHelper.INSTANCE.isReturnBreakStatement(stats.get(stats.size() - 1))) {
			final List<CommandConditionEntry> conds = getsuperConditionalStatements(ifStat);
			conds.add(0, new CommandConditionEntry(ifStat.getCondition()));
			cmds.add(new Command(new CommandStatmtEntry(true, stats), conds, exec));
		}

		if(elseStat!=null && !otherConds.stream().filter(c -> !elseStat.getElements(new FindElementFilter(c, true)).isEmpty()).findFirst().isPresent()) {
			// For the else block, creating a negation of the condition.
			stats = new ArrayList<>();

			if(elseStat instanceof CtStatementList)
				stats.addAll(((CtStatementList)elseStat).getStatements());
			else
				stats.add(elseStat);

			if(stats.size()>1 || !stats.isEmpty() && !SpoonHelper.INSTANCE.isReturnBreakStatement(stats.get(stats.size() - 1))) {
				final List<CommandConditionEntry> conds = getsuperConditionalStatements(ifStat);
				conds.add(0, new CommandConditionEntry(elseStat, SpoonHelper.INSTANCE.negBoolExpression(ifStat.getCondition())));
				cmds.add(new Command(new CommandStatmtEntry(true, stats), conds, exec));
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
		if((listenerMethod.getBody() == null || listenerMethod.getBody().getStatements().isEmpty()) &&
			(!(listenerMethod instanceof CtLambda) || ((CtLambda<?>)listenerMethod).getExpression() == null)) {// A lambda may not have a body but an expression
			// Empty so no command
			synchronized(commands) { commands.put(listenerMethod, Collections.emptyList()); }
		}else {
			final List<CtElement> conds = getConditionalStatements(listenerMethod, listenerClass);

			if(conds.isEmpty()) {
				// when no conditional, the content of the method forms a command.
				synchronized(commands) {
					if(listenerMethod.getBody()==null && listenerMethod instanceof CtLambda<?>) {
						// It means it is a lambda
						commands.put(listenerMethod, Collections.singletonList(
							new Command(new CommandStatmtEntry(true, Collections.singletonList(((CtLambda<?>)listenerMethod).getExpression())),
										Collections.emptyList(), listenerMethod)));
					} else {
						// It means it is a method
						commands.put(listenerMethod, Collections.singletonList(
							new Command(new CommandStatmtEntry(true, listenerMethod.getBody().getStatements()), Collections.emptyList(), listenerMethod)));
					}
				}
			}else {
				// For each conditional statements found in the listener method or in its dispatched methods,
				// a command is extracted.
				conds.forEach(cond -> extractCommandsFromConditionalStatements(cond, listenerMethod, conds));

				// Treating the potential code block located after the last conditional statement
				final List<Command> cmds = commands.get(listenerMethod);
				// Getting the line number of the last statement used in a command or in a conditional block.
				final int start = cmds.parallelStream().mapToInt(c -> c.getLineEnd()).max().orElseGet(() ->
							conds.parallelStream().mapToInt(c -> c.getPosition().getEndLine()).max().orElse(Integer.MAX_VALUE));
				// Getting the line code of the end of the listener method
				final int end = listenerMethod.getBody().getPosition().getEndLine();
				// Getting all the statements located in between the start and end code lines.
				// returns, throws and catch blocks are ignored.
				final List<CtStatement> finalBlock = listenerMethod.getBody().getElements(new LinePositionFilter(start, end)).
					parallelStream().filter(s -> !(s instanceof CtReturn) && !(s instanceof CtThrow) && s.getParent(CtCatch.class)==null).
					collect(Collectors.toList());

				// If there is such statements.
				if(!finalBlock.isEmpty()) {
					// If all the commands have a return statement at their end, it means that this block will form another command.
					if(cmds.parallelStream().filter(c -> c.getMainStatmtEntry().isPresent()).map(c -> c.getMainStatmtEntry().get()).
						allMatch(c -> !c.statmts.isEmpty() && c.statmts.get(c.statmts.size() - 1) instanceof CtReturn)) {
						cmds.add(new Command(new CommandStatmtEntry(true, finalBlock), Collections.emptyList(), listenerMethod));
					}else {
						// If no command has a return statement at their end, it means that this block will be part of each of these
						// commands.
						if(cmds.parallelStream().filter(c -> c.getMainStatmtEntry().isPresent()).map(c -> c.getMainStatmtEntry().get()).
							noneMatch(c -> !c.statmts.isEmpty() && c.statmts.get(c.statmts.size() - 1) instanceof CtReturn)) {
							cmds.forEach(c -> c.addAllStatements(Collections.singletonList(new CommandStatmtEntry(false, finalBlock))));
						}
						// For the other case (some of the commands have a return but some others not), we cannot manage that.
					}
				}
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
					exec.getElements(new ClassMethodCallFilter(exec.getParameters(), listenerClass.get(), true)).stream().
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
			List<CtStatement> elements;
			if(cond instanceof CtIf) {
				elements = ((CtIf)cond).getThenStatement().getElements(new ConditionalFilter());
			}else {
				elements = cond.getElements(new ConditionalFilter());
			}
			return !elements.isEmpty() &&
					// Ignoring 'cond'
					elements.stream().filter(cond2 -> cond!=cond2).
					anyMatch(cond2 -> conds.contains(cond2));
		}).collect(Collectors.toList()));

		return conds;
	}


	private boolean conditionalUsesGUIParam(final CtStatement stat, final List<CtParameterReference<?>> guiParams) {
		CtExpression<?> condition;

		if(stat instanceof CtIf) condition = ((CtIf) stat).getCondition();
		else if(stat instanceof CtSwitch<?>) condition = ((CtSwitch<?>) stat).getSelector();
		else condition = null;

		return condition != null && elementUsesGUIParam(condition, guiParams);
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
				//FIXME for the refactoring step, this step has to be improved.
				nonEmptyM.forEach(m -> analyseSingleListenerMethod(Optional.of(listenerClass), m));
				break;
		}
	}
}
