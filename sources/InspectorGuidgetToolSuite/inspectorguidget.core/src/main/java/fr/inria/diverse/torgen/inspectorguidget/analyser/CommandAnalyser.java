package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.filter.BasicFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.ClassMethodCallFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.ConditionalFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.FindElementFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.FindElementsFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.LocalVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.MyVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.LinePositionFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.helper.Tuple;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.DirectReferenceFilter;

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
		final Map<CtClass<?>, Set<CtMethod<?>>> methods = classProc.getAllListenerMethods();

		methods.entrySet().parallelStream().forEach(entry -> {
			if(entry.getValue().size()==1) {
				analyseSingleListenerMethod(Optional.of(entry.getKey()), entry.getValue().iterator().next());
			}else {
				analyseMultipleListenerMethods(entry.getKey(), entry.getValue());
			}
		});
		lambdaProc.getAllListenerLambdas().parallelStream().forEach(l -> analyseSingleListenerMethod(Optional.empty(), l));

		// Post-process to add statements (e.g. var def) used in commands but not present in the current command (because defined before or after)
		synchronized(commands) {
			commands.entrySet().parallelStream().forEach(entry -> entry.getValue().forEach(cmd -> {
				if(!cmd.getConditions().isEmpty()) {
					// For each command, adding the required local variable definitions.
					cmd.addAllStatements(0,
						// Looking for local variable accesses in the command
						cmd.getAllStatmts().stream().map(stat -> stat.getElements(new LocalVariableAccessFilter()).stream().
							// Selecting the local variable definitions not already contained in the command
								map(v -> v.getDeclaration()).filter(v -> cmd.getAllStatmts().stream().noneMatch(s -> s == v)).
								collect(Collectors.toList())).flatMap(s -> s.stream()).
							// For each var def, creating a command statement entry that will be added to the list of entries of the command.
								map(elt -> new CommandStatmtEntry(false, Collections.singletonList((CtCodeElement) elt))).collect(Collectors.toList()));

					inferLocalVarUsages(cmd, entry.getValue(), entry.getKey());
				}
			}));
		}

		synchronized(commands) {
			commands.entrySet().forEach(entry -> {
				entry.getValue().removeIf(cmd -> !cmd.hasRelevantCommandStatement());

				List<Command> badcmd = entry.getValue().stream().filter(cmd -> !cmd.getMainStatmtEntry().isPresent() ||
										cmd.getMainStatmtEntry().get().getStatmts().isEmpty()).collect(Collectors.toList());
				badcmd.forEach(cmd -> LOG.log(Level.SEVERE, "Invalid command extracted: " + cmd));
				if(!badcmd.isEmpty()) entry.getValue().removeAll(badcmd);
			});
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
			).parallelStream().filter(exp -> !cmd.hasStatement(exp) && !isPartOfMainCommandBlockOrCondition(exp, cmds)).
			map(exp -> new CommandStatmtEntry(false, Collections.singletonList(exp instanceof CtStatement ? exp : exp.getParent(CtStatement.class)))).
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
							exps.add(SpoonHelper.INSTANCE.getParentOf(varaccess, CtExpression.class, listener).orElse(varaccess));
							exps.add(var.getDeclaration());
							return exps;
						}).flatMap(s -> s.stream())
				).flatMap(s -> s)).flatMap(s -> s).filter(s -> s!=null).distinct().collect(Collectors.toSet());

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
	 * @param cmds The set of commands to look into.
	 * @return True if the given element is contained in the main block or in a condition statement of a command.
	 */
	private boolean isPartOfMainCommandBlockOrCondition(final @NotNull CtElement elt, final @NotNull List<Command> cmds) {
		final FindElementFilter filter = new FindElementFilter(elt, false);

		// First, check the main blocks
		boolean ok = cmds.parallelStream().map(cmd -> cmd.getMainStatmtEntry()). // Getting the main blocks
				// Searching for the given element in the statements of the main blocks.
					anyMatch(main -> main.isPresent() && main.get().getStatmts().stream().anyMatch(stat -> !stat.getElements(filter).isEmpty()));

		// If not found, check the conditions.
		if(!ok) {
			ok = cmds.parallelStream().anyMatch(cmd -> cmd.getConditions().stream().anyMatch(cond ->// Searching for the given element in the conditions.
				!cond.realStatmt.getElements(filter).isEmpty() ||
					cond.realStatmt!=cond.effectiveStatmt && !cond.effectiveStatmt.getElements(filter).isEmpty()));
		}

		return ok;
	}


	private void extractCommandsFromConditionalStatements(final @NotNull CtElement condStat, final @NotNull CtExecutable<?> listenerMethod,
														  final @NotNull List<CtStatement> conds) {
		List<Command> cmds;
		synchronized(commands) {
			cmds = commands.computeIfAbsent(listenerMethod, k -> new ArrayList<>());
		}

		if(condStat instanceof CtIf) {
			extractCommandsFromIf((CtIf) condStat, cmds, listenerMethod, conds);
			return;
		}

		if(condStat instanceof CtCase<?>) {
			extractCommandsFromSwitchCase((CtCase<?>) condStat, cmds, listenerMethod);
			return;
		}

		LOG.log(Level.SEVERE, "Unsupported conditional blocks: " + condStat);
	}


	private void extractCommandsFromSwitchCase(final @NotNull CtCase<?> cas, final @NotNull List<Command> cmds, final @NotNull CtExecutable<?> exec) {
		// Ignoring the case statements that are empty or that contains irrelevant statements.
		SpoonHelper.INSTANCE.getNonEmptySwitchCase(cas).
			filter(theCase -> SpoonHelper.INSTANCE.hasRelevantCommandStatements(cas.getStatements(), exec)).
			ifPresent(theCase -> {
				final List<CtElement> stats = new ArrayList<>(theCase.getStatements());
				CtSwitch<?> swit = (CtSwitch<?>) theCase.getParent();
				final List<CommandConditionEntry> conds = getsuperConditionalStatements(swit);
				conds.add(0, new CommandConditionEntry(cas, SpoonHelper.INSTANCE.createEqExpressionFromSwitchCase(swit, cas)));
				//For each case, a condition is created using the case value.
				cmds.add(new Command(new CommandStatmtEntry(true, stats), conds, exec));
		});
	}


	private void extractCommandsFromIf(final @NotNull CtIf ifStat, final @NotNull List<Command> cmds, final @NotNull CtExecutable<?> exec,
									   final @NotNull List<CtStatement> otherConds) {
		final CtStatement elseStat =  ifStat.getElseStatement();
		final CtStatement thenStat = ifStat.getThenStatement();
		List<CtElement> stats = new ArrayList<>();

		if(thenStat instanceof CtStatementList) {
			stats.addAll(((CtStatementList) thenStat).getStatements());
		} else {
			if(thenStat != null) {
				stats.add(thenStat);
			}
		}

		if(stats.size()>1 || !stats.isEmpty() && !SpoonHelper.INSTANCE.isReturnBreakStatement(stats.get(stats.size() - 1))) {
			final List<CommandConditionEntry> conds = getsuperConditionalStatements(ifStat);
			conds.add(0, new CommandConditionEntry(ifStat.getCondition()));
			cmds.add(new Command(new CommandStatmtEntry(true, stats), conds, exec));
		}

		if(elseStat!=null && otherConds.stream().allMatch(c -> elseStat.getElements(new FindElementFilter(c, true)).isEmpty())) {
			// For the else block, creating a negation of the condition.
			stats = new ArrayList<>();

			if(elseStat instanceof CtStatementList) {
				stats.addAll(((CtStatementList) elseStat).getStatements());
			}else {
				stats.add(elseStat);
			}

			if(SpoonHelper.INSTANCE.hasRelevantCommandStatements(stats, exec)) {
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
				}else {
					if(ctif.getElseStatement() == currElt) {
						conds.add(new CommandConditionEntry(condition, SpoonHelper.INSTANCE.negBoolExpression(condition)));
					}else {
						LOG.log(Level.SEVERE, "Cannot find the origin of the statement in the if statement " +
							SpoonHelper.INSTANCE.formatPosition(parent.getPosition()) + " + : " + parent);
					}
				}
			}else if(parent instanceof CtSwitch<?>) {
				final CtElement elt = currElt;
				CtSwitch<?> ctswitch = (CtSwitch<?>) parent;
				// Identifying the case statement used and creating a condition.
				// The use of orElse(null) is mandatory here (berk!) to avoid a strange compilation bug with generics and casting.
				CtCase<?> caz = ctswitch.getCases().stream().filter(cas -> cas == elt).findFirst().orElse(null);

				if(caz==null) {
					LOG.log(Level.SEVERE, "Cannot find the origin of the statement " + elt + " in the switch statement " +
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
			final List<CtStatement> conds = getConditionalStatements(listenerMethod, listenerClass, new HashSet<>());

			if(conds.isEmpty()) {
				// when no conditional, the content of the method forms a command.
				List<Command> list = new ArrayList<>();
				if(listenerMethod.getBody()==null && listenerMethod instanceof CtLambda<?>) {
					// It means it is a lambda
					list.add(new Command(new CommandStatmtEntry(true, Collections.singletonList(((CtLambda<?>)listenerMethod).getExpression())),
						Collections.emptyList(), listenerMethod));
					synchronized(commands) { commands.put(listenerMethod, list); }
				} else {
					// It means it is a method
					list.add(new Command(new CommandStatmtEntry(true, listenerMethod.getBody().getStatements()), Collections.emptyList(), listenerMethod));
					synchronized(commands) { commands.put(listenerMethod, list); }
				}
			}else {
				// For each conditional statements found in the listener method or in its dispatched methods,
				// a command is extracted.
				conds.forEach(cond -> extractCommandsFromConditionalStatements(cond, listenerMethod, conds));

				// Treating the potential code block located after the last conditional statement
				List<Command> cmds;

				synchronized(commands) {
					cmds = commands.get(listenerMethod);
				}

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


	private @NotNull List<CtStatement> getConditionalStatements(final @Nullable CtExecutable<?> exec,
																final @NotNull Optional<CtClass<?>> listenerClass,
															    final @NotNull Set<CtExecutable<?>> execAnalysed) {
		if(exec==null || exec.getBody()==null) {
			return Collections.emptyList();
		}

		final List<Tuple<CtStatement, CtStatement>> conds = new ArrayList<>();

		if(listenerClass.isPresent()) { // Searching for dispatched methods is not performed on lambdas.
			conds.addAll(
				// Getting all the methods called in the current method that use a parameter of this last.
				// The goal is to identify the dispatched methods, recursively.
			exec.getElements(new ClassMethodCallFilter(exec.getParameters(), listenerClass.get(), true)).stream().
				filter(dispatchM -> !execAnalysed.contains(dispatchM.getExecutable().getDeclaration())).
				// For each dispatched methods, looking for conditional statements.
					map(dispatchM -> {
					final CtExecutable<?> theExec = dispatchM.getExecutable().getDeclaration();
					execAnalysed.add(theExec);
					return getConditionalStatements(theExec, listenerClass, execAnalysed).stream().map(v -> new Tuple<>(v, (CtStatement)dispatchM));
				}).flatMap(s -> s).collect(Collectors.toList()));
		}

		final List<CtParameterReference<?>> guiParams = exec.getParameters().stream().map(param -> param.getReference()).collect(Collectors.toList());
		final CtBlock<?> mainBlock = exec.getBody();

		// Getting all the conditional statements
		conds.addAll(mainBlock.getElements(new ConditionalFilter()).stream().
						// Keeping those making use of a GUI parameter.
						filter(cond -> conditionalUsesGUIParam(cond, guiParams, mainBlock)).
						// a listener may be defined into the current listener.
						// So, removing the conditional statements that are not contained in the current executable.
						filter(cond -> cond.getParent(CtExecutable.class)==exec).
						// The conditionals statements that are empty are removed not to be considered furthermore.
						filter(cond -> !(cond instanceof CtIf) || !SpoonHelper.INSTANCE.isEmptyIfStatement((CtIf)cond)).
						map(cond -> {
							if(cond instanceof CtIf) {
								return Collections.singletonList(cond);
							}
							return ((CtSwitch<?>)cond).getCases().stream().map(ca -> (CtStatement)ca).collect(Collectors.toList());
						}).flatMap(s -> s.stream()).
						map(v -> new Tuple<>(v, v)).
						collect(Collectors.toList()));

		final Set<CtStatement> condsSet = conds.stream().map(c -> c.b).collect(Collectors.toSet());

		conds.removeAll(conds.parallelStream().filter(cond -> {
			List<CtStatement> elements;
			if(cond.a instanceof CtIf) {
				final CtIf ctIf = (CtIf) cond.a;
				elements = ctIf.getThenStatement()==null ? Collections.emptyList() : ctIf.getThenStatement().getElements(new FindElementsFilter<>(condsSet));
			}else {
				elements = cond.a.getElements(new FindElementsFilter<>(condsSet));
			}

			elements.remove(cond.b);
			return !elements.isEmpty();
		}).collect(Collectors.toList()));

		return conds.stream().map(t -> t.a).collect(Collectors.toList());
	}


	private boolean conditionalUsesGUIParam(final CtStatement stat, final List<CtParameterReference<?>> guiParams, final CtBlock<?> mainBlock) {
		final CtExpression<?> condition = stat instanceof CtIf ? ((CtIf) stat).getCondition() : stat instanceof CtSwitch<?> ? ((CtSwitch<?>) stat).getSelector() : null;
		return condition != null && elementUsesGUIParam(condition, guiParams, mainBlock);
	}


	private boolean elementUsesGUIParam(final CtElement elt, final List<CtParameterReference<?>> guiParams, final CtBlock<?> mainBlock) {
		// Check whether a GUI parameter is directly used in the statement.
		if(guiParams.stream().anyMatch(param -> !elt.getReferences(new DirectReferenceFilter<>(param)).isEmpty())) {
			return true;
		}

		// Otherwise, looking for local variables that use a GUI parameter.
		return elt.getElements(new LocalVariableAccessFilter()).stream().
			anyMatch(var ->
				// Maybe the declaration of the variable refers to a GUI parameter
				elementUsesGUIParam(var.getDeclaration(), guiParams, mainBlock) ||
					// or an assignment of this variable in the main block refers to a GUI parameter
					// 1. Looking for the assignments in the block
					mainBlock.getElements(new BasicFilter<>(CtAssignment.class)).stream().
						// 2. Keeping only the variable write
						anyMatch(assig -> assig.getAssigned() instanceof CtVariableWrite<?> &&
						// 3. Checking that the assigned variable is our current variable
						((CtVariableWrite<?>)assig.getAssigned()).getVariable().equals(var) &&
							// 4. Checking that the assignment directly or indirectly refers to GUI parameter
							elementUsesGUIParam(assig.getAssignment(), guiParams, mainBlock)));
	}


	private void analyseMultipleListenerMethods(final @NotNull CtClass<?> listenerClass, final @NotNull Set<CtMethod<?>> listenerMethods) {
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
				nonEmptyM.forEach(m -> analyseSingleListenerMethod(Optional.of(listenerClass), m));
				break;
		}
	}
}
