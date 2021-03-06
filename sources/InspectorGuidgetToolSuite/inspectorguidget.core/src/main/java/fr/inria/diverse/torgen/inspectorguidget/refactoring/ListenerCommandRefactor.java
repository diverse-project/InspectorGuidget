package fr.inria.diverse.torgen.inspectorguidget.refactoring;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandConditionEntry;
import fr.inria.diverse.torgen.inspectorguidget.analyser.CommandWidgetFinder;
import fr.inria.diverse.torgen.inspectorguidget.filter.BasicFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.FindElementFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.MyVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.ThisAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.filter.VariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;

/**
 * Refactors GUI listener that contain multiple commands to extract these last in
 * dedicated listeners.
 */
public class ListenerCommandRefactor {
	public static final Logger LOG = Logger.getLogger("ListenerCommandRefactor");

	private final boolean asLambda;
	private final boolean asField;
	private final Command cmd;
	private @NotNull CommandWidgetFinder.WidgetFinderEntry widgets;
	private final @NotNull Set<CtType<?>> refactoredTypes;
	private final boolean collectTypes;
	private final @NotNull Collection<CommandWidgetFinder.WidgetFinderEntry> allEntries;
	private Set<WidgetProcessor.WidgetUsage> usages;

	public ListenerCommandRefactor(final @NotNull Command command, final @NotNull CommandWidgetFinder.WidgetFinderEntry entry,
								   final boolean refactAsLambda, final boolean refactAsField, final boolean collectRefactoredTypes,
								   final @NotNull Collection<CommandWidgetFinder.WidgetFinderEntry> entries) {
		super();
		asLambda = refactAsLambda;
		widgets = entry;
		cmd = command;
		asField = refactAsField;
		collectTypes = collectRefactoredTypes;
		allEntries = entries;

		if(collectRefactoredTypes) {
			refactoredTypes = new HashSet<>();
		}else {
			refactoredTypes = Collections.emptySet();
		}
	}

	public void execute() {
		collectRefactoredType(cmd.getExecutable());

		// Removing the possible return located at the end of the listener.
		if(!cmd.getExecutable().getBody().getStatements().isEmpty() &&
			SpoonHelper.INSTANCE.isReturnBreakStatement(cmd.getExecutable().getBody().getLastStatement())) {
			LOG.log(Level.INFO, () -> cmd + ": removing the return: " + cmd.getExecutable().getBody().getLastStatement());
			cmd.getExecutable().getBody().getLastStatement().delete();
		}

		usages = widgets.getWidgetUsages(allEntries);
		usages.forEach(usage -> {
			// Getting the registration of the widgets
			final Set<CtAbstractInvocation<?>> invoks = findWidgetRegistrations(usage);
			final Set<CtAbstractInvocation<?>> unregInvoks = invoks.stream().filter(inv ->
				inv.getExecutable().getSimpleName().contains("remove")).collect(Collectors.toSet());
			LOG.log(Level.INFO, "Removing (un-)registration of widgets: " + unregInvoks);
			invoks.removeAll(unregInvoks);

			if(invoks.size()==1) {
				final CtAbstractInvocation<?> invokReg = invoks.iterator().next();
				final OptionalInt posOpt = getListenerRegPositionInInvok(invokReg);

				if(posOpt.isPresent()) {
					final int pos = posOpt.getAsInt();
					final CtExpression<?> oldParam = invokReg.getArguments().get(pos);

					if(asLambda) {
						refactorRegistrationAsLambda(invokReg, unregInvoks, pos, usage.widgetVar.getSimpleName());
					}else {
						refactorRegistrationAsAnonClass(invokReg, unregInvoks, pos, usage.widgetVar.getSimpleName());
					}
					removeOldCommand(invokReg, oldParam, pos);
				}else {
					LOG.log(Level.SEVERE, "Cannot find the position of the registration parameter: " + invokReg);
				}
			} else {
				LOG.log(Level.SEVERE, "Cannot find a unique widget registration: " + cmd + " " + invoks);
			}
		});
	}


	private OptionalInt getListenerRegPositionInInvok(final CtAbstractInvocation<?> exec) {
		final CtTypeReference<?> ref = cmd.getExecutable().getParent(CtType.class).getReference();
		final List<CtExpression<?>> args = exec.getArguments();
		return args.stream().filter(ty -> ref.equals(ty.getType())).mapToInt(ty -> args.indexOf(ty)).findAny();
	}


	private @NotNull Set<CtAbstractInvocation<?>> findWidgetRegistrations(final @NotNull WidgetProcessor.WidgetUsage usage) {
		final CtExecutable<?> cmdExec = cmd.getExecutable();
		final Filter<CtAbstractInvocation<?>> filter = new BasicFilter<CtAbstractInvocation<?>>(CtAbstractInvocation.class) {
			@Override
			public boolean matches(final CtAbstractInvocation<?> exec) {
				final OptionalInt pos = getListenerRegPositionInInvok(exec);
				return pos.isPresent() && WidgetHelper.INSTANCE.isListenerClass(exec.getExecutable().getParameters().get(pos.getAsInt()),
					exec.getFactory(), WidgetHelper.INSTANCE.getListenerInterface(cmdExec).orElse(null));
			}
		};

		return usage.getUsagesWithCons().stream().
			// gathering their parent statement.
				map(acc -> SpoonHelper.INSTANCE.getStatement(acc)).filter(stat -> stat.isPresent()).map(stat -> stat.get()).
			// Gathering the method call that matches listener registration: single parameter that is a listener type.
				map(stat -> stat.getElements(filter)).flatMap(s -> s.stream()).collect(Collectors.toSet());
	}


	/**
	 * Removes the old command.
	 * @param invok The invocation that registers the listener.
	 * @param oldParam The parameter of the invocation.
	 */
	private void removeOldCommand(final @NotNull CtAbstractInvocation<?> invok, final @NotNull CtExpression<?> oldParam, final int regPos) {
		cmd.getMainStatmtEntry().get().getStatmts().forEach(elt -> {
			LOG.log(Level.INFO, () -> cmd + ": removing the old command: " + elt);
			elt.delete();
		});

		final List<CommandConditionEntry> conds = cmd.getConditions();

		if(!conds.isEmpty()) {
			// Removing the main conditional statement.
			final CtCodeElement mainCond = conds.get(0).realStatmt;

			if(mainCond instanceof CtCase<?>) {
				LOG.log(Level.INFO, () -> "Removing the main condition of a case: " + mainCond);
				mainCond.delete();

				// On switch case, have to check whether the switch parent is empty.
				if(mainCond.isParentInitialized() && mainCond.getParent() instanceof CtSwitch &&
					SpoonHelper.INSTANCE.isEmptySwitch((CtSwitch<?>)mainCond.getParent(), cmd.getExecutable())) {
					LOG.log(Level.INFO, () -> "Removing the parent of a case: " + mainCond.getParent());
					mainCond.getParent().delete();
				}
			}else {
				final CtElement parent = mainCond.getParent();

				if(parent instanceof CtIf && SpoonHelper.INSTANCE.isEmptyIfStatement((CtIf)parent)) {
					LOG.log(Level.INFO, () -> "Removing the CtStatement of a case: " + mainCond.getParent());
					mainCond.getParent(CtStatement.class).delete();
				}
			}

			// Removing the super conditional statement only if they are empty.
			IntStream.range(1, conds.size()).mapToObj(i -> SpoonHelper.INSTANCE.getStatement(conds.get(i).realStatmt)).
				filter(stat -> stat.isPresent()).map(stat -> stat.get()).
				filter(stat -> stat instanceof CtIf && SpoonHelper.INSTANCE.isEmptyIfStatement((CtIf)stat) ||
					stat instanceof CtSwitch<?> && SpoonHelper.INSTANCE.isEmptySwitch((CtSwitch<?>)stat, cmd.getExecutable())).
				forEach(stat -> {
					LOG.log(Level.INFO, () -> cmd + ": removing the empty old cmd conditional: " + stat);
					stat.delete();
			});
		}

		removingLocalVarsIfs();

		if(cmd.getExecutable().getBody().getStatements().isEmpty()) {
			LOG.log(Level.INFO, () -> cmd + ": removing the listener method: " + cmd.getExecutable());
			cmd.getExecutable().delete();
			final CtTypeReference<?> typeRef = invok.getExecutable().getParameters().get(regPos).getTypeDeclaration().getReference();
			LOG.log(Level.INFO, () -> cmd + ": removing the implemented interface " + typeRef + " from " + cmd.getExecutable().getParent(CtType.class).getSimpleName());
			cmd.getExecutable().getParent(CtType.class).getSuperInterfaces().remove(typeRef);
		}

		// The parameter of the registration invocation may be a variable of an external listener.
		// If so, the variable is deleted.
		if(oldParam instanceof CtVariableRead) {
			final CtVariableReference<?> var = ((CtVariableRead<?>) oldParam).getVariable();

			if(var instanceof CtLocalVariableReference) {
				final CtLocalVariable<?> varDecl = ((CtLocalVariableReference<?>) var).getDeclaration();

				final List<CtVariableAccess<?>> elements = var.getParent(CtBlock.class).getElements(new MyVariableAccessFilter(varDecl));

				if(elements.isEmpty()) {
					LOG.log(Level.INFO, () -> cmd + ": removing the listener variable: " + varDecl);
					varDecl.delete();
				}
			}
		}
	}

	private void removingLocalVarsIfs() {
		final CtBlock<?> body = cmd.getExecutable().getBody();
		List<CtIf> iffs = body.getElements(new BasicFilter<>(CtIf.class));
		int oldsize;
		final IntegerProperty cpt = new SimpleIntegerProperty();
		final Set<CtElement> remain = cmd.getStatements().stream().filter(stat -> !stat.isMainEntry()).flatMap(s -> s.getStatmts().stream()).collect(Collectors.toSet());

		do {
			// Removing all the empty if statements.
			cpt.set(0);
			oldsize = iffs.size();
			iffs.stream().filter(ctif ->
				(ctif.getThenStatement() == null || ctif.getThenStatement() instanceof CtBlock && ((CtBlock<?>) ctif.getThenStatement()).getStatements().isEmpty()) &&
					(ctif.getElseStatement() == null || ctif.getElseStatement() instanceof CtBlock && ((CtBlock<?>) ctif.getElseStatement()).getStatements().isEmpty())).
				forEach(ctif -> ctif.delete());
			iffs = body.getElements(new BasicFilter<>(CtIf.class));

			// Removing all the unused local vars
			final Set<CtElement> toRemove = new HashSet<>();
			remain.stream().filter(r -> r instanceof CtLocalVariable).forEach(var -> {
				if(body.getElements(new MyVariableAccessFilter((CtVariable<?>) var)).stream().
					allMatch(access -> remain.stream().allMatch(r -> r != var && r.getElements(new FindElementFilter(access, false)).isEmpty()))) {
					var.delete();
					cpt.set(cpt.get() + 1);
					toRemove.add(var);
				}
			});
			remain.removeAll(toRemove);
			// Doing that until no more removal.
		}while(cpt.get()>0 || iffs.size()!=oldsize);
	}


	private void collectRefactoredType(final @Nullable CtElement elt) {
		if(collectTypes) {
			final CtType<?> root = SpoonHelper.INSTANCE.getMainTypeFromElt(elt);
			if(root!=null) {
				refactoredTypes.add(root);
			}
		}
	}

	private void removeLastBreakReturn(final @NotNull List<CtElement> stats) {
		if(!stats.isEmpty() && SpoonHelper.INSTANCE.isReturnBreakStatement(stats.get(stats.size()-1))) {
			LOG.log(Level.INFO, () -> cmd + ": removing a return/break: " + stats.get(stats.size()-1));
			stats.remove(stats.size()-1);
		}
	}

	private void removeActionCommandStatements() {
		usages.forEach(usage -> {
			Filter<CtInvocation<?>> filter = new BasicFilter<CtInvocation<?>>(CtInvocation.class) {
				@Override
				public boolean matches(final CtInvocation<?> element) {
					return WidgetHelper.INSTANCE.ACTION_CMD_METHOD_NAMES.stream().anyMatch(elt -> element.getExecutable().getSimpleName().equals(elt));
				}
			};

			// Getting all the set action command and co statements.
			List<CtStatement> actionCmds = usage.accesses.stream().map(access -> access.getParent(CtStatement.class)).
				filter(stat -> stat != null && !stat.getElements(filter).isEmpty()).collect(Collectors.toList());

			// Deleting each set action command and co statement.
			actionCmds.forEach(stat -> {
				LOG.log(Level.INFO, () -> cmd + ": removing setActionCmd: " + stat);
				stat.delete();
				collectRefactoredType(stat);
			});

			// Deleting the unused private/protected/package action command names defined as constants or variables (analysing the
			// usage of public variables is time-consuming).
			actionCmds
				.stream()
				.filter(cmds -> cmds instanceof CtInvocation<?>)
				// Considering the arguments of the invocation only.
				.map(invok -> ((CtInvocation<?>)invok).getArguments()).flatMap(s -> s.stream())
				.map(arg -> arg.getElements(new VariableAccessFilter())).flatMap(s -> s.stream())
				.map(access -> access.getVariable().getDeclaration())
				.filter(var -> !(var instanceof CtEnumValue))
				.distinct()
				.filter(var -> var!=null && (var.getVisibility()==ModifierKind.PRIVATE || var.getVisibility()==ModifierKind.PROTECTED ||
					var.getVisibility()==null) && SpoonHelper.INSTANCE.extractUsagesOfVar(var).size()<2)
				.forEach(var -> {
					LOG.log(Level.INFO, () -> cmd + ": removing action cmd names: " + var);
					var.delete();
					collectRefactoredType(var);
				});
		});
	}


	private void changeNonLocalMethodInvocations(final @NotNull List<CtElement> stats, final @NotNull CtAbstractInvocation<?> regInvok) {
		final Filter<CtInvocation<?>> filter = new BasicFilter<>(CtInvocation.class);
		// Getting the class where the listener is registered.
		final CtType<?> listenerRegClass = regInvok.getParent(CtType.class);
		// Getting all the invocations used in the statements.
		final List<CtInvocation<?>> nonLocalInvoks = stats
				.stream()
				.map(stat -> stat.getElements(filter))
				.flatMap(s -> s.stream())
			// Keeping the invocations that are on fields
				.filter(invok -> invok.getTarget() instanceof CtFieldRead &&
					((CtFieldRead<?>) invok.getTarget()).getVariable().getDeclaration()!=null &&
			// Keeping the invocations that calling fields are not part of the class that registers the listener.
					((CtFieldRead<?>) invok.getTarget()).getVariable().getDeclaration().getParent(CtType.class) != listenerRegClass)
			.collect(Collectors.toList());

		// The invocation may refer to a method that is defined in the class where the registration occurs.
		nonLocalInvoks.stream().filter(invok -> invok.getTarget().getType().getDeclaration()==listenerRegClass).
			// In this case, the target of the invocation (the field read) is removed since the invocation will be moved to
			// the registration class.
			forEach(invok -> {
				LOG.log(Level.INFO, () -> cmd + ": removing the target of the invocation: " + invok);
				invok.setTarget(null);
			});

		// The invocation may refer to a attribute that is defined in the listener class but no where the registration occurs.
		nonLocalInvoks.stream().filter(invok -> invok.getTarget() instanceof CtFieldRead<?>).
			// Getting such fields.
			map(invok -> ((CtFieldRead<?>)invok.getTarget()).getVariable().getDeclaration()).
			filter(f -> f!=null).distinct().forEach(field -> {
				// Extracting the local usages of these fields
				field.getParent(CtClass.class).getElements(new MyVariableAccessFilter(field)).stream().
					map(u -> u instanceof CtStatement ? (CtStatement)u : u.getParent(CtStatement.class)).
					// Only considering the initialisation of these fields for the moment.
					filter(stat -> stat.getParent(CtConstructor.class)!=null).
					forEach(u -> {
						LOG.log(Level.INFO, () -> cmd + ": moving a listener attribute: " + u + " before " + regInvok);
						if(regInvok.getParent(CtBlock.class).getStatements().parallelStream().noneMatch(s -> s.equals(u))) {
							if(regInvok instanceof CtStatement) {
								((CtStatement)regInvok).insertBefore(u.clone()); // and moving it where the initialisation occurs.
							}
						}
					});

				LOG.log(Level.INFO, () -> cmd + ": moving a listener attribute: " + field + " from " + field.getParent(CtType.class).getSimpleName() + " to " + listenerRegClass.getSimpleName());
				if(listenerRegClass.getField(field.getSimpleName())==null) {
					listenerRegClass.addField(field.clone());
				}
		});
	}


	private void changeNonLocalFieldAccesses(final @NotNull List<CtElement> stats, final @NotNull CtAbstractInvocation<?> regInvok) {
		final Filter<CtFieldRead<?>> filter = new BasicFilter<>(CtFieldRead.class);
		// Getting the class where the listener is registered.
		final CtType<?> listenerRegClass = regInvok.getParent(CtType.class);
		// Getting all the invocations used in the statements.
		final List<CtFieldRead<?>> nonLocalFieldAccesses = stats
			.stream()
			.map(stat -> stat.getElements(filter))
			.flatMap(s -> s.stream())
			// Keeping the invocations that are on fields
			.filter(f -> f.getVariable().getDeclaration()!=null &&
					// Keeping the invocations that calling fields are not part of the class that registers the listener.
					f.getVariable().getDeclaration().getParent(CtType.class) != listenerRegClass)
			.collect(Collectors.toList());

		// The invocation may refer to a method that is defined in the class where the registration occurs.
		nonLocalFieldAccesses
			.stream()
			.filter(field -> {
				try {
					return field.getType().getDeclaration() == listenerRegClass;
				}catch(final ParentNotInitializedException ex) {
					return false;
				}
			})
			// In this case, the target of the invocation (the field read) is removed since the invocation will be moved to
			// the registration class.
			.forEach(f -> {
				LOG.log(Level.INFO, () -> cmd + ": removing a field access by its target: " + f);
				f.replace(f.getTarget());
			});
	}


	/**
	 * The statements of the command may refer to the external listener through 'this'. In such a case, the 'this' has to be changed
	 * in a variable access.
	 */
	private void changeThisAccesses(final @NotNull List<CtElement> stats, final @NotNull CtAbstractInvocation<?> regInvok) {
		final Filter<CtThisAccess<?>> filter = new ThisAccessFilter(false);
		final CtExecutable<?> regMethod = regInvok.getParent(CtExecutable.class);

		stats
			.stream()
			.map(stat -> stat.getElements(filter))
			.flatMap(s -> s.stream())
			.forEach(th -> {
				final List<CtLocalVariable<?>> thisVar = regMethod.getElements(new BasicFilter<>(CtLocalVariable.class) {
					@Override
					public boolean matches(final CtLocalVariable<?> element) {
						final CtType<?> decl = element.getType().getDeclaration();
						return decl != null && decl.equals(th.getType().getDeclaration());
					}
				});

			if(thisVar.isEmpty()) {
				LOG.log(Level.SEVERE, "Cannot find a local variable for a 'this' access: " + regInvok);
			}else {
				LOG.log(Level.INFO, "Changing a this access: " + thisVar);
				th.replace(regInvok.getFactory().Code().createVariableRead(thisVar.get(0).getReference(), false));
			}
		});
	}


	private <T> void refactorRegistrationAsAnonClass(final @NotNull CtAbstractInvocation<T> regInvok,
													 final @NotNull Set<CtAbstractInvocation<?>> unregInvoks,
													 final int regPos, final String widgetName) {
		final Factory fac = regInvok.getFactory();
		final CtTypeReference<T> typeRef = (CtTypeReference<T>)regInvok.getExecutable().getParameters().get(regPos).getTypeDeclaration().getReference();
		final CtClass<T> anonCl = fac.Core().createClass();
		final CtNewClass<T> newCl = fac.Core().createNewClass();
		final List<CtElement> stats = cleanStatements(regInvok, fac);

		final Optional<CtMethod<?>> m1 = regInvok.getExecutable().getParameters().get(0).getTypeDeclaration().getMethods().stream().
			filter(meth -> meth.getBody() == null).findFirst();

		if(!m1.isPresent()) {
			LOG.log(Level.SEVERE, "Cannot find an abstract method in the listener interface: " + cmd + " " + regInvok.getExecutable());
			return;
		}

		final CtMethod<T> meth = (CtMethod<T>)m1.get().clone();
		final CtBlock<T> block = fac.Core().createBlock();
		final CtConstructor<T> cons = fac.Core().createConstructor();
		cons.setBody(fac.Core().createBlock());
		cons.setImplicit(true);
		meth.setBody(block);
		meth.getParameters().get(0).setSimpleName(cmd.getExecutable().getParameters().get(0).getSimpleName());
		meth.setModifiers(Collections.singleton(ModifierKind.PUBLIC));
		stats.stream().filter(stat -> stat instanceof CtStatement).forEach(stat -> block.insertEnd((CtStatement)stat));

		anonCl.setConstructors(Collections.singleton(cons));
		anonCl.setMethods(Collections.singleton(meth));
		anonCl.setSuperInterfaces(Collections.singleton(typeRef));
		anonCl.setSimpleName("1");
		newCl.setAnonymousClass(anonCl);

		final CtExecutableReference<T> ref = cons.getReference();
		ref.setType(typeRef);
		newCl.setExecutable(ref);

		replaceRegistrationParameter(newCl, regInvok, unregInvoks, regPos, widgetName);
		changeThisAccesses(stats, regInvok);
	}


	private List<CtElement> cleanStatements(final @NotNull CtAbstractInvocation<?> regInvok, final @NotNull Factory fac) {
		List<CtElement> stats = cmd.getAllLocalStatmtsOrdered().stream().map(stat -> stat.clone()).collect(Collectors.toList());

		removeLastBreakReturn(stats);
		removeActionCommandStatements();
		changeNonLocalMethodInvocations(stats, regInvok);
		changeNonLocalFieldAccesses(stats, regInvok);
		// Removing the unused local variables of the command.
		removeUnusedLocalVariables(stats);

		final List<CtParameterReference<?>> guiParams = cmd.getExecutable().getParameters().stream().map(param -> param.getReference()).collect(Collectors.toList());
		final CtBlock<?> mainBlock = cmd.getExecutable().getBody();

		// Adding conditional statements that are not used to identify the widget
		for(int i=0, size=cmd.getConditions().size(); i<size; i++) {
			final CtExpression<Boolean> cond = cmd.getConditions().get(i).createBoolExp();

			if(!CommandAnalyser.conditionalUsesGUIParam(cmd.getConditions().get(i).realStatmt.getParent(), guiParams, mainBlock)) {
				final CtIf iff = fac.Core().createIf();
				final CtBlock<?> block = fac.Core().createBlock();
				iff.setCondition(cond.clone());
				stats.stream().filter(stat -> stat instanceof CtStatement).forEach(stat -> block.insertEnd((CtStatement) stat));
				iff.setThenStatement(block);
				stats = Collections.singletonList(iff);
			}
		}

		return stats;
	}


	private <T> void refactorRegistrationAsLambda(final @NotNull CtAbstractInvocation<?> regInvok,
												  final @NotNull Set<CtAbstractInvocation<?>> unregInvoks,
												  final int regPos, final String widgetName) {
		final Factory fac = regInvok.getFactory();
		final CtTypeReference<T> typeRef = (CtTypeReference<T>) regInvok.getExecutable().getParameters().get(regPos).getTypeDeclaration().getReference();
		final CtLambda<T> lambda = fac.Core().createLambda();
		final List<CtElement> stats = cleanStatements(regInvok, fac);

		if(stats.size()==1 && stats.get(0) instanceof CtExpression<?>) {
			lambda.setExpression((CtExpression<T>)stats.get(0));
		} else {
			final CtBlock<T> block = fac.Core().createBlock();
			stats.stream().filter(stat -> stat instanceof CtStatement).forEach(stat -> block.insertEnd((CtStatement)stat));
			lambda.setBody(block);
		}

		final CtParameter<?> oldParam = cmd.getExecutable().getParameters().get(0);
		final CtParameter<?> param = fac.Executable().createParameter(lambda, oldParam.getType(), oldParam.getSimpleName());
		lambda.setParameters(Collections.singletonList(param));
		lambda.setType(typeRef);

		replaceRegistrationParameter(lambda, regInvok, unregInvoks, regPos, widgetName);
		changeThisAccesses(stats, regInvok);
	}


	private <T> void replaceRegistrationParameter(final @NotNull CtExpression<T> elt,
												  final @NotNull CtAbstractInvocation<?> regInvok,
												  final @NotNull Set<CtAbstractInvocation<?>> unregInvoks,
												  final int regPos, final String widgetName) {
		final Factory fac = regInvok.getFactory();

		if(asField && cmd.getExecutable() instanceof CtMethod<?> || !unregInvoks.isEmpty()) {
			final CtField<T> newField = fac.Core().createField();
			newField.setSimpleName(widgetName + "Cmd");
			newField.addModifier(ModifierKind.FINAL);
			newField.setVisibility(ModifierKind.PRIVATE);
			newField.setType(elt.getType().clone());
			newField.setAssignment(elt);
			cmd.getExecutable().getParent(CtClass.class).addFieldAtTop(newField);
			regInvok.getArguments().get(regPos).replace(SpoonHelper.INSTANCE.createField(fac, newField));
			unregInvoks.forEach(unreg -> getListenerRegPositionInInvok(unreg).ifPresent(pos ->
				unreg.getArguments().get(pos).replace(SpoonHelper.INSTANCE.createField(fac, newField))));
		}else {
			regInvok.getArguments().get(regPos).replace(elt);
		}

		collectRefactoredType(regInvok);
		unregInvoks.forEach(unreg -> collectRefactoredType(unreg));
	}


	/**
	 * Removed the unused local variables declared in the given statements.
	 * Variable accesses are identified to check whether local variables are no more used.
	 * The algorithm continues to check until no more local variables are removed.
	 * @param stats The statements to analyse.
	 */
	private void removeUnusedLocalVariables(final @NotNull List<CtElement> stats) {
		cmd.getMainStatmtEntry().ifPresent(mainEntry -> {
			// Getting the main statements of the command.
			final List<CtElement> mainStats = mainEntry.getStatmts();
			// Gathering all the variables required by the main statements.
			final Set<CtVariableReference<?>> varsMain = mainStats.stream().
				map(stat -> SpoonHelper.INSTANCE.getAllLocalVarDeclaration(stat)).flatMap(s -> s.stream()).
				map(var -> (CtVariableReference<?>)var.getReference()).collect(Collectors.toSet());
			final VariableAccessFilter filter = new VariableAccessFilter();

			stats.removeIf(stat -> {
				final boolean toRemove = // Must ignore the local variables used by the main statements.
					!(stat instanceof CtLocalVariable<?> && varsMain.contains(((CtLocalVariable<?>)stat).getReference())) &&
						// Must ignore the invocations
						!(stat instanceof CtInvocation) &&
						// Must ignore the main statements.
						!mainStats.contains(stat) &&
						// Checking whether the statement uses a required variable.
						stat.getElements(filter).stream().noneMatch(access -> varsMain.contains(access.getVariable()));

				if(toRemove) {
					LOG.log(Level.INFO, () -> cmd + ": removing a statement: " + stat);
				}

				return toRemove;
			});
		});
	}


	public Set<CtType<?>> getRefactoredTypes() {
		return Collections.unmodifiableSet(refactoredTypes);
	}
}
