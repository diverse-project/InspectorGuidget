package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.filter.MyVariableAccessFilter;
import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.helper.WidgetHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.VariableAccessFilter;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Detects declaration of widgets.
 */
public class WidgetProcessor extends InspectorGuidgetProcessor<CtTypeReference<?>> {
	/** The widgets instiantiation and their usages. */
	private final @NotNull Set<WidgetUsage> widgetUsages;
	/** The widgets created and directly added in a container. */
	private final @NotNull Set<CtInvocation<?>> refWidgets;

	private Collection<CtTypeReference<?>> controlType;
	private final boolean withConfigStat;
	/** A cache used to optimise the type refWidgets to analyse. */
	private final Map<String, Boolean> cacheTypeChecked;

	public WidgetProcessor() {
		this(false);
	}

	public WidgetProcessor(final boolean withConfigurationStatmts) {
		super();
		widgetUsages = new HashSet<>();
		refWidgets = new HashSet<>();
		withConfigStat = withConfigurationStatmts;
		cacheTypeChecked = new HashMap<>();
	}

	@Override
	public void init() {
		LOG.log(Level.INFO, "init processor " + getClass().getSimpleName());
		controlType = WidgetHelper.INSTANCE.getWidgetTypes(getFactory());
	}

	@Override
	public void processingDone() {
		// Now have to extract the usages of each widgets.

		// Grouping all the widgets by their var definition.
		// Some widgets may be redefined into an existing variable (used for another widget).
		// In this case the list<widgetusage> corresponding to the ctvariable will contain 2 elements. The constructor
		// differenciates the widgets.
		// The next line does not work since Identify is required and groupBy has to use widgetVar
//		final Map<CtVariable<?>, List<WidgetUsage>> usages = widgetUsages.parallelStream().collect(Collectors.groupingBy(u -> u.widgetVar));
		final Map<CtVariable<?>, List<WidgetUsage>> usages = new IdentityHashMap<>();
		widgetUsages.forEach(u -> {
			List<WidgetUsage> usage = usages.computeIfAbsent(u.widgetVar, k -> new ArrayList<>());
			usage.add(u);
		});


		// For each variable, computing its usages
		List<WidgetUsage> finalUsages = usages.entrySet().parallelStream().map(entry -> {
			// We suppose the usage cannot be empty because of the constructor call.
			if(entry.getValue().isEmpty()) {
				LOG.log(Level.SEVERE, () -> "This variable does not have widget usage: " + entry.getValue());
				return Collections.<WidgetUsage>emptyList();
			}

			if(entry.getValue().size() == 1) {
				WidgetUsage u1 = entry.getValue().get(0);
				// Getting the usages of this widget.
				return Collections.singletonList(new WidgetUsage(u1.widgetVar, u1.creation.orElse(null), extractUsagesOfWidgetVar(u1.widgetVar)));
			}else {
				if(entry.getValue().stream().anyMatch(u -> !u.creation.isPresent())){
					LOG.log(Level.SEVERE, () -> "A constructor is not defined while several usages are present: " + entry.getValue());
					return Collections.<WidgetUsage>emptyList();
				}

				// Have to find out which statement is part of which widget using their position in the code.
				final List<CtVariableAccess<?>> allUsages = extractUsagesOfWidgetVar(entry.getValue().get(0).widgetVar);
				// Sorting the widget using the position of their initialisation.
				final List<WidgetUsage> widgetSorted = entry.getValue().stream().sorted(
					(a, b) -> a.creation.get().getPosition().getLine() < b.creation.get().getPosition().getLine() ? -1 : 1).
					collect(Collectors.toList());

				int min = 0;
				int max;
				List<WidgetUsage> finalWidgetUsages = new ArrayList<>();

				// For each widget usage, computing which lines are part of it.
				for(int i = 0, size = widgetSorted.size(); i<size; i++) {
					max = i==size-1 ? Integer.MAX_VALUE : widgetSorted.get(i + 1).creation.get().getPosition().getLine();
					final int fmin = min;
					final int fmax = max;
					finalWidgetUsages.add(new WidgetUsage(widgetSorted.get(i).widgetVar, widgetSorted.get(i).creation.orElse(null),
						allUsages.stream().filter(u -> u.getPosition().getLine() >= fmin && u.getPosition().getLine() < fmax).collect(Collectors.toList())));
					min = max;
				}

				return finalWidgetUsages;
			}
		}).flatMap(s -> s.stream()).collect(Collectors.toList());

		widgetUsages.clear();
		widgetUsages.addAll(finalUsages);

		// Removing the initialisation from the usages.
		widgetUsages.parallelStream().filter(u -> u.creation.isPresent()).forEach(u -> {
			try {
				final CtElement consCallParent = u.creation.get().getParent();

				if(consCallParent instanceof CtAssignment<?, ?>) {
					List<CtVariableAccess<?>> varCreation = consCallParent.getElements(new MyVariableAccessFilter(u.widgetVar));
					u.accesses.removeAll(varCreation);
				}
			}catch(ParentNotInitializedException ex) {
				LOG.log(Level.SEVERE, "The parent of " + u.creation.get() + " is not initialised.", ex);
			}
		});

		super.processingDone();
		cacheTypeChecked.clear();
	}

	public @NotNull Set<WidgetUsage> getWidgetUsages() {
		return widgetUsages;
	}

	public @NotNull Set<CtInvocation<?>> getRefWidgets() {
		return refWidgets;
	}

	@Override
	public boolean isToBeProcessed(final CtTypeReference<?> type) {
		String ty = type.getQualifiedName();

		if(cacheTypeChecked.containsKey(ty)) {
			return cacheTypeChecked.get(ty);
		}else {
			boolean ok = isASubTypeOf(type, controlType);
			cacheTypeChecked.put(ty, ok);
			return ok;
		}
	}

	@Override
	public void process(final @NotNull CtTypeReference<?> element) {
		final CtElement parent = element.getParent();

		LOG.log(Level.INFO, () -> "PROCESSING " + element + " " + parent.getClass());

		if(parent instanceof CtField<?> || parent instanceof CtLocalVariable<?>) {
			onWidgetVar((CtVariable<?>) parent);
			return;
		}
		if(parent instanceof CtExecutableReference<?> && parent.getParent() instanceof CtConstructorCall<?>) {
			analyseWidgetConstructorCall((CtConstructorCall<?>) parent.getParent());
			return;
		}
		if(parent instanceof CtAssignment<?,?>) {
			analyseWidgetAssignment((CtAssignment<?, ?>) parent);
			return;
		}
		if(parent instanceof CtFieldReference<?>) {
			CtField<?> decl = ((CtFieldReference<?>) parent).getDeclaration();

			if(decl!=null && WidgetHelper.INSTANCE.isTypeRefAWidget(decl.getType())) {
				onWidgetVar(decl);
			}
			return;
		}
		if(parent instanceof CtMethod<?>) {
			analyseMethodUse((CtMethod<?>) parent);
			return;
		}
		if(parent instanceof CtExecutableReference<?>) {
			// A method is called on a widget, so ignored.
			return;
		}
		if(parent instanceof CtTypeAccess<?>) {
			// A static method / attribute is used on a widget class.
			return;
		}
		if(parent instanceof CtClass<?>) {
			// A widget class is used.
			return;
		}

		if(parent instanceof CtLocalVariableReference<?> || parent instanceof CtVariableRead<?>) {
			// A widget var assigned to a local var; or simply used.
			return;
		}

		if(parent instanceof CtThisAccess<?>) {
			// Use of this on a widget object.
			return;
		}

		LOG.log(Level.WARNING, "CTypeReference parent not supported or ignored: " + parent.getClass() + " " + parent);
	}


	private void analyseMethodUse(final @NotNull CtMethod<?> meth) {
		final ModifierKind visib = meth.getVisibility();

		if(visib == ModifierKind.PRIVATE) {
			meth.getParent(CtClass.class).getElements(new InvocationFilter(meth)).forEach(invok -> analyseWidgetInvocation(invok));
		}else if(visib == ModifierKind.PUBLIC) {
			meth.getFactory().Package().getRootPackage().getElements(new InvocationFilter(meth)).forEach(invok -> analyseWidgetInvocation(invok));
		}else if(visib == null || visib == ModifierKind.PROTECTED) {
			meth.getParent(CtPackage.class).getElements(new InvocationFilter(meth)).forEach(invok -> analyseWidgetInvocation(invok));
		}
	}


	private void processConstructorCallInVar(final @NotNull CtVariable<?> var, final @NotNull CtConstructorCall<?> call) {
		synchronized(widgetUsages) {
			final List<WidgetUsage> widgets = widgetUsages.parallelStream().filter(u -> u.widgetVar == var).collect(Collectors.toList());

			// The constructor must not be already present in the widget usages.
			if(widgets.stream().noneMatch(u -> u.creation.isPresent() && u.creation.get()==call)) {
				if(widgets.size() == 1 && !widgets.get(0).creation.isPresent()) {
					WidgetUsage widgetUsage = widgets.get(0);
					widgetUsages.remove(widgetUsage);
					widgetUsages.add(new WidgetUsage(var, call, widgetUsage.accesses));
				} else {
					// The var is already created. So another widget usage will be created
					widgetUsages.add(new WidgetUsage(var, call, Collections.emptyList()));
				}
			}
		}
	}

	private void analyseWidgetConstructorCall(final @NotNull CtConstructorCall<?> call) {
		if(call.isParentInitialized()) {
			final CtElement parent = call.getParent();

			// When the creation of the widget is stored in a new local var, this var is considered as a widget.
			if(parent instanceof CtVariable<?>) {
				processConstructorCallInVar((CtVariable<?>)parent, call);
			}
			// When the creation of the widget is stored in an already defined local var...
			else if(parent instanceof CtAssignment<?,?>) {
				CtAssignment<?, ?> assig = (CtAssignment<?, ?>) parent;

				if(assig.getAssigned() instanceof CtVariableAccess<?>) {
					processConstructorCallInVar(((CtVariableAccess<?>)assig.getAssigned()).getVariable().getDeclaration(), call);
				}
			}
			else if(parent instanceof CtInvocation<?>) {
				onWidgetCreatedInContainer((CtInvocation<?>) parent);
			}
		}
	}


	private void analyseWidgetUse(final CtElement elt, final CtTypeReference<?> refType) {
		if(elt instanceof CtAssignment<?, ?>) {
			analyseWidgetAssignment((CtAssignment<?, ?>) elt);
			return;
		}
		if(elt instanceof CtInvocation<?>) {
			analyseWidgetInvocation((CtInvocation<?>) elt);
			return;
		}

		if(elt instanceof CtLocalVariable<?>) {
			analyseUseOfLocalVariable(((CtLocalVariable<?>)elt).getReference(), elt.getParent(CtBlock.class), refType);
			return;
		}

		LOG.log(Level.WARNING, "Widget use not supported or ignored (" + SpoonHelper.INSTANCE.formatPosition(elt.getPosition()) + "): " + elt.getClass());
	}


	private void analyseUseOfLocalVariable(final @NotNull CtLocalVariableReference<?> var, final @Nullable CtBlock<?> block, final CtTypeReference<?> refType) {
		if(block==null) {
			LOG.log(Level.SEVERE, "No block ("+ SpoonHelper.INSTANCE.formatPosition(var.getPosition())+"): " + var);
			return;
		}

		block.getElements(new VariableAccessFilter<>(var)).forEach(access -> analyseWidgetUse(access.getParent(), refType));
	}


	/**
	 * Object foo;
	 * foo = new JButton();
	 */
	private void analyseWidgetAssignment(final @NotNull CtAssignment<?,?> assign) {
		final CtExpression<?> exp = assign.getAssigned();

		if(exp instanceof CtFieldWrite<?>) {
			onWidgetVar(((CtFieldWrite<?>) exp).getVariable().getDeclaration());
		}
		else if(exp instanceof CtVariableWrite<?>) {
			onWidgetVar(((CtVariableWrite<?>)exp).getVariable().getDeclaration());
		}
		else {
			LOG.log(Level.WARNING, "Widget Assignment not supported or ignored: " + exp.getClass() + " " + exp);
		}
	}


	/**
	 * JPanel panel = new JPanel();
	 * panel.add(new JWindow());
	 * ****
	 * List<Object> foo = new ArrayList<>();
	 * foo.add(new JMenuItem());
	 */
	private void analyseWidgetInvocation(final @NotNull CtInvocation<?> invok) {
		final CtExpression<?> exp = invok.getTarget();

		if(exp==null) {
			LOG.log(Level.WARNING, "Cannot treat the widget invocation because of a null type: " + invok);
			return;
		}

		final CtTypeReference<?> type = exp.getType();

		if(isASubTypeOf(type, controlType)) {
			onWidgetCreatedInContainer(invok);
		}
		else if(invok.getParent() instanceof CtAssignment<?,?>) {
			analyseWidgetAssignment((CtAssignment<?, ?>) invok.getParent());
		}

		LOG.log(Level.WARNING, "Widget invocation not supported or ignored: " + type + " " + invok);
	}


	public boolean isWidgetVarUsed(final @Nullable CtVariable<?> var) {
		if(var==null) return false;
		synchronized(widgetUsages) {
			return widgetUsages.parallelStream().anyMatch(u -> u.widgetVar == var);
		}
	}


	private void onWidgetCreatedInContainer(final @Nullable CtInvocation<?> invocation) {
		if(invocation!=null) {
			synchronized(refWidgets) {
				refWidgets.add(invocation);
			}
		}
	}

	private void onWidgetVar(final @Nullable CtVariable<?> var) {
		if(var!=null && !isWidgetVarUsed(var)) {
			synchronized(widgetUsages) {
				widgetUsages.add(new WidgetUsage(var, null, Collections.emptyList()));
			}
		}
	}

	private List<CtVariableAccess<?>> extractUsagesOfWidgetVar(final CtVariable<?> var) {
		if(withConfigStat) {
			return SpoonHelper.INSTANCE.extractUsagesOfVar(var);
		}
		return Collections.emptyList();
	}


	public static class WidgetUsage {
		public final @NotNull CtVariable<?> widgetVar;
		public final @NotNull Optional<CtConstructorCall<?>> creation;
		public final @NotNull List<CtVariableAccess<?>> accesses;

		public WidgetUsage(final @NotNull CtVariable<?> widgetVar, final @Nullable CtConstructorCall<?> creation,
						   final @NotNull List<CtVariableAccess<?>> accesses) {
			this.widgetVar = widgetVar;
			this.creation = Optional.ofNullable(creation);
			this.accesses = accesses;
		}

		public @NotNull List<CtCodeElement> getUsagesWithCons() {
			if(accesses.isEmpty()) {
				return creation.isPresent() ? Collections.singletonList(creation.get()) : Collections.emptyList();
			}
			final List<CtCodeElement> stats = new ArrayList<>((List<CtCodeElement>)(List<?>)accesses);
			creation.ifPresent(cons -> stats.add(cons));
			return stats;
		}

		@Override
		public boolean equals(final Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			WidgetUsage that = (WidgetUsage) o;

			if(!widgetVar.equals(that.widgetVar)) return false;
			if(!widgetVar.getPosition().equals(that.widgetVar.getPosition())) return false;
			if(creation.isPresent() != that.creation.isPresent()) return false;
			return !creation.isPresent() ||
				creation.get().equals(that.creation.get()) &&
				creation.get().getPosition().equals(that.creation.get().getPosition()) &&
				accesses.equals(that.accesses);

		}

		@Override
		public int hashCode() {
			int result = widgetVar.hashCode();
			result = 31 * result + widgetVar.getPosition().hashCode();
			if(creation.isPresent()) {
				result = 31 * result + creation.get().hashCode();
				result = 31 * result + creation.get().getPosition().hashCode();
			}

			result = 31 * result + accesses.hashCode();
			return result;
		}

		@Override
		public String toString() {
			String creat = creation.flatMap(c ->
				Optional.of(c.toString() + " " + SpoonHelper.INSTANCE.formatPosition(c.getPosition()))).orElse("nope");
			return "WidgetUsage{var: " + widgetVar + " (" + SpoonHelper.INSTANCE.formatPosition(widgetVar.getPosition()) +
				"), construct: " + creat + ", nbAccessses:" + accesses.size() + "}";
		}
	}
}
