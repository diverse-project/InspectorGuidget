package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.Tuple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandWidgetBugsDetector {
	private final @NotNull Map<Command, CommandWidgetFinder.WidgetFinderEntry> cmds;
	private final @NotNull List<Tuple<String, Command>> results;

	public CommandWidgetBugsDetector(final @NotNull Map<Command, CommandWidgetFinder.WidgetFinderEntry> commands) {
		super();
		cmds = commands;
		results = new ArrayList<>();
	}

	public void process() {
		cmds.entrySet().parallelStream().forEach(entry -> {
			checkOneWidgetNoCondition(entry).ifPresent(res -> results.add(res));
		});
	}

	private Optional<Tuple<String, Command>> checkOneWidgetNoCondition(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
		if(entry.getValue().getNbDistinctWidgets()<2 && !entry.getKey().getConditions().isEmpty())
			return Optional.of(new Tuple<>("A single or no widget registred to the listener, but conditions are defined.", entry.getKey()));
		return Optional.empty();
	}

	private Optional<Tuple<String, Command>> checkOneWidgetRegisteredSeveralUsed(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
		if(entry.getValue().getRegisteredWidgets().size()<entry.getValue().getWidgetsUsedInConditions().size())
			return Optional.of(new Tuple<>("The command uses more widgets than the number of widgets registered to the listener.", entry.getKey()));
		return Optional.empty();
	}

	private Optional<Tuple<String, Command>> checkWidgetUsedAreOfRegistered(final @NotNull Map.Entry<Command, CommandWidgetFinder.WidgetFinderEntry> entry) {
		if(!entry.getValue().getRegisteredWidgets().stream().filter(w -> entry.getValue().getWidgetsUsedInConditions().contains(w)).findFirst().isPresent())
			return Optional.of(new Tuple<>("The widgets registered to the listener do not match the widgets used by the command.", entry.getKey()));
		return Optional.empty();
	}
}
