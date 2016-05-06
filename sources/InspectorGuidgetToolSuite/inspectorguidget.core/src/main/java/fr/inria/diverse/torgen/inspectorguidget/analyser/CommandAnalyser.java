package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import org.jetbrains.annotations.NotNull;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.VariableAccessFilter;

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
		//TODO support methods of the class called in listener methods.
	}


	private void analyseParameters(final @NotNull List<CtParameter<?>> params, final @NotNull CtExecutable<?> listenerMethod) {
		params.parallelStream().forEach(par -> {
			getConditionalThatUseVarRef(par.getReference(), listenerMethod);
		});
	}


	private List<CtElement> getConditionalThatUseVarRef(final CtVariableReference<?> varRef,
														final @NotNull CtExecutable<?> listenerMethod) {
		final CtBlock<?> body = listenerMethod.getBody();
		final List<CtVariableAccess<?>> use = body.getElements(new VariableAccessFilter<>(varRef));

		use.forEach(varAcc -> {
			// Two cases:
			// 1/ the parameter is used in a conditional statement;
			SpoonHelper.INSTANCE.getConditionalParent(varAcc, body).ifPresent(cond -> {
				extractCommandsFromConditionalStatements(cond, listenerMethod);
			});
			// 2/ The parameter is given as argument to a method of the listener class (dispatch of the event process)
			//TODO
		});

		return Collections.emptyList();
	}


	private void extractCommandsFromConditionalStatements(final @NotNull CtElement condStat, final @NotNull CtExecutable<?> listenerMethod) {
		//TODO analyse nested conditional statements
		//TODO analyse local variables used in the conditions and that use GUI event parameters.
		// Object foo = e.getSource();
		// if(foo instance JButton) {}
		List<Command> cmds = commands.get(listenerMethod);

		if(cmds==null) {
			cmds = new ArrayList<>();
			commands.put(listenerMethod, cmds);
		}

		if(condStat instanceof CtIf) {
			final CtIf ifStat = (CtIf) condStat;
			final CtBlock<?> elseStat =  ifStat.getElseStatement();
			cmds.add(new Command(((CtBlock<?>)ifStat.getThenStatement()).getStatements()));

			if(elseStat!=null) {
				//TODO create a command if it does not contain any other GUI conditional statement
				cmds.add(new Command(elseStat.getStatements()));
			}
			return;
		}

		//TODO switch, ternary
		LOG.log(Level.SEVERE, "Unsupported conditional blocks: " + condStat);
	}


	private void analyseSingleListenerMethod(final Optional<CtClass<?>> listenerClass, final @NotNull CtExecutable<?> listenerMethod) {
		if(listenerMethod.getBody()==null || listenerMethod.getBody().getStatements().isEmpty()) {
			// Empty so no command
			synchronized(commands) { commands.put(listenerMethod, Collections.emptyList()); }
		}else {
			final List<CtStatement> conds = SpoonHelper.INSTANCE.getConditionalStatements(listenerMethod);

			if(conds.isEmpty()) {
				// when no conditional, the content of the method forms a command.
				synchronized(commands) {
					commands.put(listenerMethod, Collections.singletonList(new Command(listenerMethod.getBody().getStatements())));
				}
			}else {
				analyseParameters(listenerMethod.getParameters(), listenerMethod);
			}
		}
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
