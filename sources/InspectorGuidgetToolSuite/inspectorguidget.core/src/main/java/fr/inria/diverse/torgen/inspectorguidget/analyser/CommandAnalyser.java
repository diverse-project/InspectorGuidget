package fr.inria.diverse.torgen.inspectorguidget.analyser;

import fr.inria.diverse.torgen.inspectorguidget.helper.SpoonHelper;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;

import java.util.*;

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

		final Set<CtLambda<?>> lambdas = lambdaProc.getAllListenerLambdas();

		lambdas.parallelStream().forEach(lambda -> {
			analyseSingleListenerMethod(Optional.empty(), lambda);
		});
	}


	private void analyseSingleListenerMethod(final Optional<CtClass<?>> listenerClass, final CtExecutable<?> listenerMethod) {
		if(listenerMethod.getBody()==null || listenerMethod.getBody().getStatements().isEmpty()) {
			// Empty so no command
			synchronized(commands) { commands.put(listenerMethod, Collections.emptyList()); }
		}else {
			final List<CtStatement> conds = SpoonHelper.INSTANCE.getConditionalStatements(listenerMethod);
			//TODO
		}
	}


	private void analyseMultipleListenerMethods(final CtClass<?> listenerClass, final List<CtMethod<?>> listenerMethods) {
		//TODO
	}


	// Get statements in conditionals
	public List<CtCodeElement> processConditionalStmts(CtCodeElement line) {
		List<CtCodeElement> res=new ArrayList<>();

		if(line instanceof CtIf) {
			CtStatement thenStatement=((CtIf) line).getThenStatement();
			CtStatement elseStatement=((CtIf) line).getElseStatement();
			res.add(thenStatement);
			if(elseStatement != null) {
				res.add(elseStatement);
			}
		}else if(line instanceof CtConditional) {// Need to fix it! It cannot
			// be cast
			CtConditional<?> conditional=(CtConditional<?>) line;
			res.add(conditional.getThenExpression());
			if(conditional.getElseExpression() != null) {
				res.add(conditional.getElseExpression());
			}
		}else if(line instanceof CtDo) {
			CtDo ctDo=(CtDo) line;
			res.add(ctDo.getBody());

		}else if(line instanceof CtForEach) {
			CtForEach forEach=(CtForEach) line;
			res.add(forEach.getBody());

		}else if(line instanceof CtFor) {
			CtFor for_=(CtFor) line;
			res.add(for_.getBody());
		}else if(line instanceof CtSwitch) {
			CtSwitch switch_=(CtSwitch) line;
			List<CtCase<?>> cases=switch_.getCases();
			for(CtCase<?> case_ : cases) {
				if(case_ != null) {
					res.addAll(case_.getStatements());
				}
			}
		}else if(line instanceof CtTry) {
			CtTry try_=(CtTry) line;
			res.addAll(try_.getBody().getStatements());

		}else if(line instanceof CtWhile) {
			CtWhile while_=(CtWhile) line;
			res.add(while_.getBody());

		}else if(line instanceof CtBlock) {
			CtBlock<?> block=(CtBlock<?>) line;
			res.addAll(block.getStatements());
		}else if(line instanceof CtSynchronized) {
			CtSynchronized sync=(CtSynchronized) line;
			CtBlock<?> block=sync.getBlock();
			res.addAll(block.getStatements());
		}
		return res;
	}

	public boolean isCondStatement(CtCodeElement stmt) {
		if(stmt instanceof CtIf || stmt instanceof CtConditional || stmt instanceof CtDo || stmt instanceof CtForEach || stmt instanceof CtFor || stmt instanceof CtSwitch || stmt instanceof CtWhile) {
			return true;
		}
		return false;
	}

	// Verify if the listener is contained by these statements
	public boolean isConditionalStatement(CtCodeElement stmt) {// Refactor and
		// change the
		// name: bad
		// name

		if(stmt instanceof CtIf || stmt instanceof CtConditional || stmt instanceof CtDo || stmt instanceof CtForEach || stmt instanceof CtFor || stmt instanceof CtSwitch || stmt instanceof CtTry || stmt instanceof CtWhile || stmt instanceof CtBlock) {
			return true;
		}
		return false;
	}

	// Get statements in conditionals
	public List<CtStatement> processCondStatements(CtCodeElement line) {// Can
		// be
		// removed
		// but
		// firstly:
		// refactor
		// in
		// VariablesProcessor
		List<CtStatement> res=new ArrayList<>();

		if(line instanceof CtIf) {

			CtStatement thenStatement=((CtIf) line).getThenStatement();
			CtStatement elseStatement=((CtIf) line).getElseStatement();
			try {
				// statements.add(ifCondition);
				res.add(thenStatement);
				res.add(elseStatement);
			}catch(NullPointerException e) {
				System.err.println("Caught NullPointerException: " + e.getMessage());
			}
		}else if(line instanceof CtConditional) {// Need to fix it! It cannot
			// be cast
			CtConditional<?> conditional=(CtConditional<?>) line;
			CtExpression<?> thenExpr=conditional.getThenExpression();
			res.add((CtStatement) thenExpr);
			res.add((CtStatement) conditional.getElseExpression());
			System.out.println("Conditional " + conditional);
		}else if(line instanceof CtDo) {
			CtDo ctDo=(CtDo) line;
			res.add(ctDo.getBody());

		}else if(line instanceof CtForEach) {
			CtForEach forEach=(CtForEach) line;
			res.add(forEach.getBody());

		}else if(line instanceof CtSwitch) {
			CtSwitch switch_=(CtSwitch) line;
			List<CtCase<?>> cases=switch_.getCases();
			for(CtCase<?> case_ : cases) {
				res.addAll(case_.getStatements());
			}

		}else if(line instanceof CtTry) {
			CtTry try_=(CtTry) line;
			res.addAll(try_.getBody().getStatements());

		}else if(line instanceof CtWhile) {
			CtWhile while_=(CtWhile) line;
			res.add(while_.getBody());

		}else if(line instanceof CtBlock) {
			CtBlock<?> block=(CtBlock<?>) line;
			res.addAll(block.getStatements());
		}else if(line instanceof CtSynchronized) {
			CtSynchronized sync=(CtSynchronized) line;
			CtBlock<?> block=sync.getBlock();
			res.addAll(block.getStatements());
		}
		return res;
	}
}
