package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;

import java.util.*;
import java.util.stream.Collectors;

public class CommandAnalyser extends InspectorGuidetAnalyser {
	private final ClassListenerProcessor classProc;
	private final LambdaListenerProcessor lambdaProc;
	private final Map<CtExecutable<?>, List<Command>> commands;

	public CommandAnalyser() {
		super(Collections.emptyList());

		commands = new IdentityHashMap<>();
		classProc=new ClassListenerProcessor();
		lambdaProc=new LambdaListenerProcessor();

		addProcessor(classProc);
		addProcessor(lambdaProc);
	}

	public Map<CtExecutable<?>, List<Command>> getCommands() {
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
	}


	private void analyseSingleListenerMethod(final Optional<CtClass<?>> listenerClass, final CtExecutable<?> listenerMethod) {
		if(listenerMethod.getBody()==null || listenerMethod.getBody().getStatements().isEmpty()) {
			// Empty so no command
			synchronized(commands) { commands.put(listenerMethod, Collections.emptyList()); }
		}else {
			final List<CtStatement> conds = SpoonHelper.INSTANCE.getConditionalStatements(listenerMethod);

			if(conds.isEmpty()) {
				// when no conditional, the content of the method forms a command.
				synchronized(commands) { commands.put(listenerMethod, Arrays.asList(new Command(listenerMethod.getBody().getStatements()))); }
			}else {
				//TODO
			}
		}
	}


	private void analyseMultipleListenerMethods(final CtClass<?> listenerClass, final List<CtMethod<?>> listenerMethods) {
		final List<CtMethod<?>> nonEmptyM = listenerMethods.stream().
				filter(l -> l.getBody()!=null && !l.getBody().getStatements().isEmpty()).collect(Collectors.toList());

		if(nonEmptyM.size()==1) {
			// Only one method used.
			analyseSingleListenerMethod(Optional.of(listenerClass), nonEmptyM.get(0));
		}else {
			//TODO
		}
	}
}
