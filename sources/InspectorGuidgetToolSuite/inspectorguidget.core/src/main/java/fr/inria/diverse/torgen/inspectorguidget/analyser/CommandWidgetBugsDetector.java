package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtVariable;

import java.util.*;

public class CommandWidgetBugsDetector {
	private final @NotNull Map<Command, CommandWidgetFinder.WidgetFinderEntry> cmds;
	private final @NotNull List<Tuple<String, Command>> results;
	private @Nullable List<CtExecutable<?>> supercalls;

	public CommandWidgetBugsDetector(final @NotNull Map<Command, CommandWidgetFinder.WidgetFinderEntry> commands) {
		super();
		cmds = commands;
		results = new ArrayList<>();
	}

	public List<Tuple<String, Command>> getResults() {
		return Collections.unmodifiableList(results);
	}

	public void process() {
		cmds.entrySet().parallelStream().forEach(entry -> {
//			checkOneWidgetNoCondition(entry).ifPresent(res -> results.add(res));
//			checkOneWidgetRegisteredSeveralUsed(entry).ifPresent(res -> results.add(res));
//			checkWidgetUsedAreOfRegistered(entry).ifPresent(res -> results.add(res));
			checkAtLeastOneWidgetForOneCommand(entry).ifPresent(res -> results.add(res));
		});
	}

//	private @NotNull List<CtExecutable<?>> getSuperCalls() {
//		if(supercalls==null) {
//			supercalls = SpoonHelper.INSTANCE.getExecUsingSuperCall(
//								cmds.keySet().parallelStream().map(cm -> cm.getExecutable()).collect(Collectors.toList()));
//		}
//		return supercalls;
//	}

//	private Optional<Tuple<String, Command>> checkOneWidgetNoCondition(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
//		if(entry.getValue().getNbDistinctWidgets()<2 && !entry.getKey().getConditions().isEmpty()) {
//			final CtExecutable<?> exec = entry.getKey().getExecutable();
//			final CtTypeReference<?> execClass = exec.getParent(CtClass.class).getReference();
//			final List<CtExecutable<?>> subs = getSuperCalls().parallelStream().
//							filter(sup -> sup.getSimpleName().equals(exec.getSimpleName()) && sup.getParent(CtClass.class).isSubtypeOf(execClass)).
//							collect(Collectors.toList());
//
//			if(subs.isEmpty())
//				return Optional.of(new Tuple<>("A single or no widget registred to the listener, but conditions are defined.", entry.getKey()));
//		}
//		return Optional.empty();
//	}

//	private Optional<Tuple<String, Command>> checkOneWidgetRegisteredSeveralUsed(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
//		if(entry.getValue().getRegisteredWidgets().size()<entry.getValue().getWidgetsUsedInConditions().size())
//			return Optional.of(new Tuple<>("The command uses more widgets than the number of widgets registered to the listener.", entry.getKey()));
//		return Optional.empty();
//	}

//	private Optional<Tuple<String, Command>> checkWidgetUsedAreOfRegistered(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
//		final CommandWidgetFinder.WidgetFinderEntry value = entry.getValue();
//		if(!value.getRegisteredWidgets().stream().filter(w -> value.getWidgetsUsedInConditions().contains(w)).findFirst().isPresent() &&
//			!value.getRegisteredWidgets().stream().map(w -> w.getDeclaration()).
//				filter(w -> value.getWidgetsFromSharedVars().contains(w)).findFirst().isPresent())
//			return Optional.of(new Tuple<>("The widgets registered to the listener do not match the widgets used by the command.", entry.getKey()));
//		return Optional.empty();
//	}

	private Optional<Tuple<String, Command>> checkAtLeastOneWidgetForOneCommand(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
		CommandWidgetFinder.WidgetFinderEntry value = entry.getValue();
		List<CtVariable<?>> ws = value.getWidgetUsedInBothRegistrationCmd();

		switch(ws.size()) {
			case 0: return Optional.of(new Tuple<>("Cannot find any widget for this command.", entry.getKey()));
			case 1: return Optional.empty();
			default:
				return Optional.of(new Tuple<>("More than one widgets found for this command. " +
					ws.size() + " widgets found that are: " + value.getRegisteredWidgets() + "(registered) " + value.getWidgetsFromStringLiterals().keySet() +
					" (string lit) " + value.getWidgetsFromSharedVars().keySet() + "(shared vars) " + value.getWidgetsUsedInConditions() + " (in cond)" +
					value.getWidgetClasses().isPresent() + " (widget classes)", entry.getKey()));
		}
	}
}
