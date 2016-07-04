package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.filter.*;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.helper.Tuple;
import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
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
	private final @NotNull Map<CtVariable<?>, List<CtVariableAccess<?>>> widgets;

	/**
	 * Craetes the analyser.
	 * @param commands the set of commands to analyse.
	 */
	public CommandWidgetFinder(final @NotNull List<Command> commands, final @NotNull Map<CtVariable<?>, List<CtVariableAccess<?>>> widgets) {
		super();
		cmds = commands;
		results = new HashMap<>();
		this.widgets = widgets;
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

//		long time = System.currentTimeMillis();
		entry.setRegisteredWidgets(getAssociatedListenerVariable(cmd));
//		System.out.println("ANALYSIS #1 in: " + (System.currentTimeMillis()-time));
//		time = System.currentTimeMillis();
		entry.setWidgetsUsedInConditions(getVarWidgetUsedInCmdConditions(cmd));
//		System.out.println("ANALYSIS #2 in: " + (System.currentTimeMillis()-time));
//		time = System.currentTimeMillis();
		entry.setWidgetClasses(getWidgetClass(cmd));
//		System.out.println("ANALYSIS #3 in: " + (System.currentTimeMillis()-time));
//		time = System.currentTimeMillis();
		entry.setWidgetsFromSharedVars(matchWidgetsUsagesWithCmdConditions(cmd));
//		System.out.println("ANALYSIS #4 in: " + (System.currentTimeMillis()-time));
//		time = System.currentTimeMillis();
		entry.setWidgetsFromStringLiterals(matchWidgetsUsagesWithStringsInCmdConditions(cmd));
//		System.out.println("ANALYSIS #5 in: " + (System.currentTimeMillis()-time));
	}


	/**
	 * Example:
	 * if(e.getActionCommand().equals("FOO")){...}
	 * ...
	 * button.setActionCommand("FOO");
	 * @param cmd The command to analyse.
	 */
	private Map<? extends CtVariable<?>, List<CtLiteral<?>>> matchWidgetsUsagesWithStringsInCmdConditions(final @NotNull Command cmd) {
		final StringLiteralFilter stringLiteralFilter = new StringLiteralFilter();

		final Set<CtLiteral<?>> stringliterals = cmd.getConditions().parallelStream().
			// Must ignore the conditions of if statements when in an else block (in this case the effective if statement is a negation of the
			// real conditions, so they are different)
				filter(cond -> cond.realStatmt==cond.effectiveStatmt || cond.realStatmt.isParentInitialized() && !(cond.realStatmt.getParent() instanceof CtIf)).
			// Getting the variables used in the conditions
				map(cond -> cond.effectiveStatmt.getElements(stringLiteralFilter)).flatMap(s -> s.stream()).
			// Keeping those that declaration are not null
			// Collecting them
				distinct().collect(Collectors.toCollection(HashSet::new));

		final Map<? extends CtVariable<?>, List<CtLiteral<?>>> widget = widgets.entrySet().stream().
			map(entry -> entry.getValue().stream().
				// Getting the code statement that uses the variable
					map(varac -> varac.getParent(CtStatement.class)).
					filter(stat -> stat != null).
				// Looking for the variables used in the conditions in the code statement
					map(stat -> stringliterals.stream().filter(varr -> !stat.getElements(new FindElementFilter(varr, false)).isEmpty()).
					collect(Collectors.toList())).
					filter(list -> !list.isEmpty()).
					map(var -> new Tuple<>(entry.getKey(), var))).
			// Collecting all the variables used in both the command's conditions and the code statements that configure widgets
				flatMap(s -> s).
				collect(Collectors.toMap(Tuple::getA, Tuple::getB, (a, b) -> Stream.concat(a.stream(), b.stream()).distinct().collect(Collectors.toList())));

		if(widget.size()>1) {
			System.err.println("MORE THAN ONE WIDGET FOUND USING STRING LITERALS: " + widgets + " " + cmd);
		}

		return widget;
	}


	/**
	 * Example:
	 * if(e.getActionCommand().equals(FOO)){...}
	 * ...
	 * button.setActionCommand(FOO);
	 * @param cmd The command to analyse.
	 */
	private Map<? extends CtVariable<?>, List<CtVariable<?>>> matchWidgetsUsagesWithCmdConditions(final @NotNull Command cmd) {
		final VariableAccessFilter filter = new VariableAccessFilter();

		final Set<CtVariable<?>> vars = cmd.getConditions().parallelStream().
			// Must ignore the conditions of if statements when in an else block (in this case the effective if statement is a negation of the
			// real conditions, so they are different)
			filter(cond -> cond.realStatmt==cond.effectiveStatmt || cond.realStatmt.isParentInitialized() && !(cond.realStatmt.getParent() instanceof CtIf)).
			// Getting the variables used in the conditions
			map(cond -> cond.effectiveStatmt.getElements(filter)).flatMap(s -> s.stream()).
			// Keeping those that declaration are not null
			map(acc -> acc.getVariable().getDeclaration()).filter(var -> var!=null).
			// Collecting them
			distinct().collect(Collectors.toCollection(HashSet::new));

		final Map<? extends CtVariable<?>, List<CtVariable<?>>> widget = widgets.entrySet().parallelStream().
			map(entry -> entry.getValue().parallelStream().filter(m -> {
				// Ignoring the statements that are parts of a listener method. The statements that must be analysed
				// or those that configure the widgets.
					try {
						CtExecutable<?> ex = m.getParent(CtExecutable.class);
						return ex==null || !WidgetHelper.INSTANCE.isListenerClassMethod(ex);
					}catch(ParentNotInitializedException ex) {
						return true;
					}
				}).
				// Getting the code statement that uses the variable
				map(varac -> varac.getParent(CtStatement.class)).
				filter(stat -> stat != null).
				// Looking for the variables used in the conditions in the code statement
				map(stat -> vars.stream().filter(varr -> !stat.getElements(new MyVariableAccessFilter<>(varr.getReference())).isEmpty()).
					collect(Collectors.toList())).
				filter(list -> !list.isEmpty()).
				map(var -> new Tuple<>(entry.getKey(), var))).
			// Collecting all the variables used in both the command's conditions and the code statements that configure widgets
			flatMap(s -> s).
			collect(Collectors.toMap(Tuple::getA, Tuple::getB, (a, b) -> Stream.concat(a.stream(), b.stream()).distinct().collect(Collectors.toList())));

		if(widget.size()>1) {
			System.err.println("MORE THAN ONE WIDGET FOUND USING VARIABLES: " + widgets + " " + cmd);
		}

		return widget;
	}


	@SuppressWarnings("rawtypes")
	private @NotNull Optional<CtClass<?>> getWidgetClass(final @NotNull Command cmd) {
		final CtExecutable<?> listenerMethod = cmd.getExecutable();
		final CtInvocation<?> inv = listenerMethod.getParent(CtInvocation.class);

		if(inv!=null || !listenerMethod.isParentInitialized() || !(listenerMethod.getParent() instanceof CtClass<?>))
			return Optional.empty();

		Optional<CtClass> ctClass = listenerMethod.getParent().getElements(new ThisAccessFilter(false)).stream().
			filter(thisacc -> thisacc.isParentInitialized() && thisacc.getParent() instanceof CtInvocation<?>).
			map(thisacc -> {
				final CtInvocation<?> invok = (CtInvocation<?>) thisacc.getParent();
				final CtExpression<?> target = invok.getTarget();
				CtClass clazz;

				if(target instanceof CtThisAccess<?> && WidgetHelper.INSTANCE.isTypeRefAToolkitWidget(invok.getExecutable().getDeclaringType()))
					clazz = (CtClass) ((CtThisAccess<?>) target).getType().getDeclaration();
				else clazz = null;

				return Optional.ofNullable(clazz);
			}).filter(clazz -> clazz.isPresent()).findFirst().orElseGet(() -> Optional.empty());

		return Optional.ofNullable(ctClass.orElseGet(() -> null));
	}


	/**
	 * Identifies the widgets used the conditions of the given command.
	 * @param cmd The comand to analyse
	 * @return The list of the references to the widgets used in the conditions.
	 */
	private @NotNull List<CtVariableReference<?>> getVarWidgetUsedInCmdConditions(final @NotNull Command cmd) {
		final TypeRefFilter filter = new TypeRefFilter(WidgetHelper.INSTANCE.getWidgetTypes(cmd.getExecutable().getFactory()));

		return cmd.getConditions().stream().map(cond -> cond.realStatmt.getElements(filter).stream().
											map(w -> {
												try{
													CtVariableReference<?> varrr = w.getParent(CtVariableReference.class);

													if(varrr!=null && WidgetHelper.INSTANCE.isTypeRefAWidget(varrr.getType())) {
														return varrr;
													}

													return null;
												}catch(ParentNotInitializedException ex) {
													return null;
												}
											}).filter(w -> w!=null)).
											flatMap(s -> s).collect(Collectors.toList());
	}


//	private Optional<CtClass<?>> getWidgetClass(final @NotNull CtThisAccess<?> thisaccess) {
//		if(WidgetHelper.INSTANCE.isTypeRefAWidget(thisaccess.getType())) {
//			return Optional.ofNullable((CtClass<?>)thisaccess.getType().getDeclaration());
//		}
//		return Optional.empty();
//	}


	/**
	 * Identifies the widget on which the listener is added.
	 * @param cmd The command to analyse.
	 * @return The reference to the widget or nothing.
	 */
	private @NotNull List<CtVariableReference<?>> getAssociatedListenerVariable(final @NotNull Command cmd) {
		final CtExecutable<?> listenerMethod = cmd.getExecutable();
		final CtInvocation<?> invok = listenerMethod.getParent(CtInvocation.class);

		if(invok==null) {
			if(listenerMethod.isParentInitialized() && listenerMethod.getParent() instanceof CtClass)
				return getAssociatedListenerVariableThroughClass((CtClass<?>)listenerMethod.getParent());
			return Collections.emptyList();
		}

		Optional<CtVariableReference<?>> lisVar = getAssociatedListenerVariableThroughInvocation(invok);
		return lisVar.isPresent() ? Collections.singletonList(lisVar.get()) : Collections.emptyList();
	}


	/**
	 * Example: myWidget.addActionListener(this)
	 * @param clazz The class to analyse.
	 * @return The possible widget.
	 */
	private List<CtVariableReference<?>> getAssociatedListenerVariableThroughClass(final @NotNull CtClass<?> clazz) {
		// Looking for 'this' usages
		List<CtVariableReference<?>> ref = clazz.getElements(new ThisAccessFilter(false)).stream().
			// Keeping the 'this' usages that are parameters of a method call
				filter(thisacc -> thisacc.isParentInitialized() && thisacc.getParent() instanceof CtInvocation<?>).
				map(thisacc -> getAssociatedListenerVariableThroughInvocation((CtInvocation<?>) thisacc.getParent())).
				filter(varref -> varref.isPresent()).map(varref -> varref.get()).collect(Collectors.toList());

		// Looking for associations in super classes.
		final CtType<?> superclass = clazz.getSuperclass()==null?null:clazz.getSuperclass().getDeclaration();
		if(superclass instanceof CtClass<?>)
			ref.addAll(getAssociatedListenerVariableThroughClass((CtClass<?>)superclass));

		return ref;
	}


	/**
	 * Example: myWidget.addActionListener(() ->...);
	 * @param invok The invocation from which the widget will be retieved.
	 * @return The possible widget.
	 */
	private Optional<CtVariableReference<?>> getAssociatedListenerVariableThroughInvocation(final @NotNull CtInvocation<?> invok) {
		if(!WidgetHelper.INSTANCE.isTypeRefAToolkitWidget(invok.getExecutable().getDeclaringType()))
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
		}else if(target instanceof CtThisAccess<?> || target instanceof CtTypeAccess<?>) {
			// First instanceof: 'This' accesses are supported in getWidgetClass.
			// Second instanceof: For example: JOptionPane.showConfirmDialog(...), so not related to a variable.
		}else {
			System.err.println("INVOCATION TARGET TYPE NOT SUPPORTED: " + target.getClass() + " : " + invok + " " +
								SpoonHelper.INSTANCE.formatPosition(invok.getPosition()));
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
		private @NotNull List<CtVariableReference<?>> registeredWidgets;
		private @NotNull List<CtVariableReference<?>> widgetsUsedInConditions;
		private @NotNull Optional<CtClass<?>> widgetClasses;
		private @NotNull Map<? extends CtVariable<?>, List<CtVariable<?>>> widgetsFromSharedVars;
		private @NotNull Map<? extends CtVariable<?>, List<CtLiteral<?>>> widgetsFromStringLiterals;

		private WidgetFinderEntry() {
			super();
			registeredWidgets = Collections.emptyList();
			widgetsUsedInConditions = Collections.emptyList();
			widgetClasses = Optional.empty();
			widgetsFromSharedVars = Collections.emptyMap();
			widgetsFromStringLiterals = Collections.emptyMap();
		}

		public @NotNull  Map<? extends CtVariable<?>, List<CtLiteral<?>>> getWidgetsFromStringLiterals() {
			return Collections.unmodifiableMap(widgetsFromStringLiterals);
		}

		public @NotNull Map<? extends CtVariable<?>, List<CtVariable<?>>> getWidgetsFromSharedVars() {
			return Collections.unmodifiableMap(widgetsFromSharedVars);
		}

		public @NotNull List<CtVariableReference<?>> getRegisteredWidgets() {
			return Collections.unmodifiableList(registeredWidgets);
		}

		public @NotNull List<CtVariableReference<?>> getWidgetsUsedInConditions() {
			return Collections.unmodifiableList(widgetsUsedInConditions);
		}

		public @NotNull Optional<CtClass<?>> getWidgetClasses() {
			return widgetClasses;
		}

		public void setWidgetsFromStringLiterals(final @NotNull Map<? extends CtVariable<?>, List<CtLiteral<?>>> widgetsFromStringLiterals) {
			this.widgetsFromStringLiterals = widgetsFromStringLiterals;
		}

		public void setWidgetsFromSharedVars(final @NotNull Map<? extends CtVariable<?>, List<CtVariable<?>>> widgetsFromSharedVars) {
			this.widgetsFromSharedVars = widgetsFromSharedVars;
		}

		private void setRegisteredWidgets(final @NotNull List<CtVariableReference<?>> registeredWidgets) {
			this.registeredWidgets = registeredWidgets;
		}

		private void setWidgetsUsedInConditions(final @NotNull List<CtVariableReference<?>> widgetsUsedInConditions) {
			this.widgetsUsedInConditions = widgetsUsedInConditions;
		}

		public void setWidgetClasses(final @NotNull Optional<CtClass<?>> widgetClasses) {
			this.widgetClasses = widgetClasses;
		}

		public long getNbDistinctWidgets() {
			return Stream.concat(Stream.concat(Stream.concat(registeredWidgets.stream().map(w -> w.getDeclaration()),
												widgetsUsedInConditions.stream().map(w -> w.getDeclaration())),
								widgetsFromSharedVars.keySet().stream()), widgetsFromStringLiterals.keySet().stream()).
					distinct().count()+(widgetClasses.isPresent()?1:0);
		}

		public List<CtVariable<?>> getDistinctUsedWidgets() {
			return Stream.concat(Stream.concat(new ArrayList<>(getWidgetsFromSharedVars().keySet()).stream().map(w -> (CtVariable<?>)w),
				new ArrayList<>(getWidgetsUsedInConditions()).stream().map(w -> w.getDeclaration())),
				new ArrayList<>(getWidgetsFromStringLiterals().keySet()).stream().map(w -> (CtVariable<?>)w)).
				distinct().collect(Collectors.toList());
		}


		public List<CtVariable<?>> getSuppostedAssociatedWidget() {
			final List<CtVariable<?>> widgets = getDistinctUsedWidgets();

			if(widgets.isEmpty()) {
				return registeredWidgets.stream().map(w -> w.getDeclaration()).collect(Collectors.toList());
			}
			return widgets;
		}

		public List<CtVariable<?>> getWidgetUsedInBothRegistrationCmd() {
			final List<CtVariable<?>> widgets = getDistinctUsedWidgets();
			return registeredWidgets.stream().map(w -> w.getDeclaration()).filter(w -> widgets.contains(w)).collect(Collectors.toList());
		}
	}
}
