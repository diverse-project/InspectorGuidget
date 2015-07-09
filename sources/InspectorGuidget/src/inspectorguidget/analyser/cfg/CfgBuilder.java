package inspectorguidget.analyser.cfg;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This class manage the creation of a control flow graph from a method
 */
public class CfgBuilder {
	
	/** 
	 * Container for created nodes
	 */
	ControlFlowGraph cfg;
	
	/**
	 * Store return statements found 
	 */
	ArrayList<SubGraph> returnStatements;
	
	/**
	 * Used to create negation on conditionals
	 */
	public static Factory factory;
	
	/**
	 * Creates a builder to process one method
	 */
	private CfgBuilder(ControlFlowGraph cfg) {
		this.cfg = cfg;
		returnStatements = new ArrayList<>();
	}
	
	/**
	 * Build the control flow graph from a list of statements.
	 * It connects the SubGraph of each statement with his predecessor
	 * and successor and have in charge the aggregation of statements to
	 * build basic blocs.
	 */
	private SubGraph process(List<CtStatement> statements){
		
		SubGraph res = new SubGraph(cfg);
		
		SubGraph previous = null;
		List<CtCodeElement> current = new ArrayList<>();
		for(CtStatement stmt : statements){
			
			if(	stmt instanceof CtConditional 	||
				stmt instanceof CtDo 		||
				stmt instanceof CtFor 		||
				stmt instanceof CtForEach	||
				stmt instanceof CtIf 		||
				stmt instanceof CtSwitch 	||
				stmt instanceof CtTry 		||
				stmt instanceof CtWhile 	||
				stmt instanceof CtBlock){
				
				//Close the previous block
				if(!current.isEmpty()){
					SubGraph block = new SubGraph(current,cfg);
					connect(res,previous, block);
					
					current = new ArrayList<>();
					previous = block;
				}
				
				//Process the statement
				SubGraph subgraph = process(stmt);
				connect(res,previous,subgraph);
				
				previous = subgraph;
				
			}
			else if( stmt instanceof CtReturn ){
				current.add(stmt);
				//Close the previous block
//				if(!current.isEmpty()){
					SubGraph block = new SubGraph(current,cfg);
					connect(res,previous, block);
					
					current = new ArrayList<>();
					previous = block;
//				}
				
//				if(previous == null){
//					res.setExit(res.getEntry());
//				}
//				else{
					returnStatements.add(previous);
					res.setExit(previous.getExit());
//				}
				return res;
			}
			else{
				current.add(stmt);
			}
		}
		
		//Close the previous block
		if(!current.isEmpty()){
			SubGraph block = new SubGraph(current,cfg);
			connect(res,previous,block);
			
			res.setExit(block.getExit());
		}
		else if(previous == null){
			res.setExit(res.getEntry());
		}
		else{
			res.setExit(previous.getExit());
		}
		
		return res;
	}
	
	/**
	 * Connect the new element with the previous element.
	 * If the previous is null, the new element become the entry of the container
	 */
	private void connect(SubGraph container, SubGraph previous, SubGraph newElement){
		if(previous != null){
			if(newElement.getEntry() instanceof Connector){
				previous.getExit().merge(newElement.getEntry());
			}
			else{
				previous.getExit().addChild(newElement.getEntry(),null);
			}
		}
		else{
			container.setEntry(newElement.getEntry());
		}
	}
	
	//TODO: manage break and continue
	/**
	 * Build a control flow graph from a do-while statement
	 */
	private SubGraph process(CtDo doWhileLoop){
		
		//Get data
		CtExpression<Boolean> condition = doWhileLoop.getLoopingExpression();
		CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
		negCondition.setOperand(condition);
		negCondition.setKind(UnaryOperatorKind.NOT);
		//negCondition.setParent(condition);
		CtStatement body = doWhileLoop.getBody();
		
		//Build the sub-graph
		SubGraph loopBody = process(body);
		ConditionalSolution continueSolutions = Solver.solve(condition.toString());
		ConditionalSolution breakSolutions = Solver.solve("!("+condition.toString()+")");
		
		SubGraph res = new SubGraph(cfg);
		res.getEntry().addChild(loopBody.getEntry(),null);
		loopBody.getExit().addChild(loopBody.getEntry(),condition);
		loopBody.getExit().addChild(res.getExit(),negCondition);

		return res;
	}
	
	//TODO: manage break and continue
	/**
	 * Build a control flow graph from a For statement
	 */
	private SubGraph process(CtFor forLoop){
		
		//Get data
		List<CtStatement> init = forLoop.getForInit();
		List<CtStatement> update = forLoop.getForUpdate();
		CtExpression<Boolean> condition = forLoop.getExpression();
		CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
		negCondition.setOperand(condition);
		negCondition.setKind(UnaryOperatorKind.NOT);
		//negCondition.setParent(condition);
		CtStatement body = forLoop.getBody();
		
		//Build the sub-graph
		ConditionalSolution continueSolutions = Solver.solve(condition.toString());
		ConditionalSolution breakSolutions = Solver.solve("!("+condition.toString()+")");
		SubGraph initBlock = process(init);
		List<CtStatement> bodyUpdater = new ArrayList<>();
		bodyUpdater.add(body);
		bodyUpdater.addAll(update);
		SubGraph loopBody = process(bodyUpdater);
		
		SubGraph res = new SubGraph(cfg);
		res.setEntry(initBlock.getEntry());
		initBlock.getExit().addChild(loopBody.getEntry(),condition);
		initBlock.getExit().addChild(res.getExit(),negCondition);
		loopBody.getExit().addChild(loopBody.getEntry(),condition);
		loopBody.getExit().addChild(res.getExit(),negCondition);
		
		return res;
	}
	
	private SubGraph process(CtForEach forEachLoop){
		//Get data
		CtExpression<?> iterable = forEachLoop.getExpression();
		CtLocalVariable<?> variable = forEachLoop.getVariable();
		CtStatement body = forEachLoop.getBody();
		
		//iterator i = 0
		CtLiteral<Object> zero = factory.Core().createLiteral(); 
		zero.setValue(0);
		CtLocalVariable<Object> i = factory.Core().createLocalVariable();
		i.setSimpleName("i"); //TODO find better name to avoid collision
		i.setDefaultExpression(zero);
		
		//condition
		CtVariableAccess<Object> iRef = factory.Core().createVariableAccess();
		iRef.setVariable(i.getReference());
		CtBinaryOperator<Boolean> condition = factory.Core().createBinaryOperator();
		condition.setKind(BinaryOperatorKind.LT);
		condition.setLeftHandOperand(iRef);
		
		 CtTypeReference<?> iterableType = iterable.getType();
		 if(iterableType instanceof CtArrayTypeReference){
			 //variable = array[i]
			 CtVariableAccess<Object> iRef0 = factory.Core().createVariableAccess();
			 iRef0.setVariable(i.getReference());
			 CtArrayAccess arrayElem = factory.Core().createArrayAccess();
			 arrayElem.setTarget(iterable);
			 arrayElem.setIndexExpression(iRef0);
			 variable.setDefaultExpression(arrayElem);
			 
			 //condition i < array.length
			 CtFieldReference<Object> length = factory.Core().createFieldReference();
			 length.setSimpleName("length");
			 CtFieldAccess<Object> getSize = factory.Core().createFieldAccess();
			 getSize.setTarget(iterable);
			 getSize.setVariable(length);
			 condition.setRightHandOperand(getSize);
		 }
		 else{
			 //variable = iterable.get(i)
			 CtInvocation get = factory.Core().createInvocation();
			 get.setTarget(iterable);
			 CtExecutableReference<Object> method = factory.Core().createExecutableReference();
			 method.setSimpleName("get");
			 get.setExecutable(method);
			 CtVariableAccess<Object> iRef0 = factory.Core().createVariableAccess();
			 iRef0.setVariable(i.getReference());
			 List<CtExpression<?>> args = new ArrayList<>();
			 args.add(iRef0);
			 get.setArguments(args);
			 variable.setDefaultExpression(get);
			
			 //condition i < iterable.size()
			 CtInvocation<Object> size = factory.Core().createInvocation();
			 size.setTarget(iterable);
			 CtExecutableReference<Object> methodSize = factory.Core().createExecutableReference();
			 methodSize.setSimpleName("size");
			 size.setExecutable(methodSize);
			 condition.setRightHandOperand(size);
		 }
		 
		//iterator i++
		CtUnaryOperator<Object> iPlusPlus = factory.Core().createUnaryOperator();
		iPlusPlus.setKind(UnaryOperatorKind.POSTINC);
		CtVariableAccess<Object> iRef2 = factory.Core().createVariableAccess();
		iRef2.setVariable(i.getReference());
		iPlusPlus.setOperand(iRef2);
		 
		CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
		negCondition.setOperand(condition);
		negCondition.setKind(UnaryOperatorKind.NOT);
		
		//////////////////////////////////////////
		List<CtStatement> init = new ArrayList<>();
		init.add(i);
		SubGraph initBlock = process(init);
		
		List<CtStatement> bodyUpdater = new ArrayList<>();
		bodyUpdater.add(variable);
		bodyUpdater.add(body);
		bodyUpdater.add(iPlusPlus);
		SubGraph loopBody = process(bodyUpdater);
		
		SubGraph res = new SubGraph(cfg);
		res.setEntry(initBlock.getEntry());
		initBlock.getExit().addChild(loopBody.getEntry(),condition);
		initBlock.getExit().addChild(res.getExit(),negCondition);
		loopBody.getExit().addChild(loopBody.getEntry(),condition);
		loopBody.getExit().addChild(res.getExit(),negCondition);
		
		return res;
	}

	/**
	 * Build a control flow graph from a cond?expr:expr statement
	 */
	private SubGraph process(CtConditional<?> conditional){
		
		//Get data
		CtExpression<Boolean> condition = conditional.getCondition();
		CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
		negCondition.setOperand(condition);
		negCondition.setKind(UnaryOperatorKind.NOT);
		//negCondition.setParent(condition);
		CtExpression<?> thenExp = conditional.getThenExpression();
//		CtExpression<?> elseExp = conditional.getElseExpression();
		
		//Build the sub-graph
		SubGraph thenBlock = process(thenExp);
		SubGraph elseBlock = process(thenExp);
		ConditionalSolution thenSolutions = Solver.solve(condition.toString());
		ConditionalSolution elseSolutions = Solver.solve("!("+condition.toString()+")");
		
		SubGraph res = new SubGraph(cfg);
		res.getEntry().addChild(thenBlock.getEntry(),condition);
		res.getEntry().addChild(elseBlock.getEntry(),negCondition);
		thenBlock.getExit().addChild(res.getExit(),null);
		thenBlock.getExit().addChild(res.getExit(),null);
		
		return res;
	}
	
	/**
	 * Build a control flow graph from a If statement
	 */
	private SubGraph process(CtIf ifConditional){
		//Get data
		CtExpression<Boolean> condition = ifConditional.getCondition();
		CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
		negCondition.setOperand(condition);
		negCondition.setKind(UnaryOperatorKind.NOT);
		//negCondition.setParent(condition);
		CtStatement thenPart = ifConditional.getThenStatement();
		CtStatement elsePart = ifConditional.getElseStatement();
		
		//Build the sub-graph
		SubGraph thenBlock = process(thenPart);
		ConditionalSolution thenCondition = Solver.solve(condition.toString());
		
		SubGraph res = new SubGraph(cfg);
		res.getEntry().addChild(thenBlock.getEntry(),condition);
		thenBlock.getExit().addChild(res.getExit(),null);
		
		//Do the same for else block
		if(elsePart!=null) {
			SubGraph elseBlock = process(elsePart);
			ConditionalSolution elseCondition = Solver.solve("!("+condition.toString()+")");
			res.getEntry().addChild(elseBlock.getEntry(),negCondition);
			elseBlock.getExit().addChild(res.getExit(),null);
		}
		else{
			res.getEntry().addChild(res.getExit(),negCondition);
		}
		
		return res;
	}
	
	/**
	 * Build a control flow graph from a Switch statement
	 */
	private SubGraph process(CtSwitch switchStatement){
		//Get data
//		CtExpression<?> selector = switchStatement.getSelector();
		List<CtCase<?>> cases = switchStatement.getCases();
		
		SubGraph res = new SubGraph(cfg);
		//Build the sub-graph
		for(CtCase<?> case_ : cases){
			SubGraph caseBlock = process(case_.getStatements());
			
			if(case_.getCaseExpression() != null){
//				String condition = "(" + selector.getSignature() + ") == (" + case_.getCaseExpression().getSignature() +")";
//				ConditionalSolution caseCondition = Solver.solve(condition);
				
				res.getEntry().addChild(caseBlock.getEntry(),case_.getCaseExpression()); //with condition
			}
			else{
				res.getEntry().addChild(caseBlock.getEntry(),null); //TODO:with negation of all conditions
			}

			caseBlock.getExit().addChild(res.getExit(),null);
		}

		return res;
	}
	
	/**
	 * Build a control flow graph from a Try-Catch-Finally statement
	 * Merge the try and the finally in one block and ignore the catch
	 */
	private SubGraph process(CtTry tryStatement){
		//Get data
		CtBlock<?> body = tryStatement.getBody();
		CtBlock<?> finalizer = tryStatement.getFinalizer();
		
		//Build the sub-graph
		List<CtStatement> block = new ArrayList<>();
		block.addAll(body.getStatements());
		
		if(finalizer != null)block.addAll(finalizer.getStatements());
		
		return process(block);
	}
	
	/**
	 * Build a control flow graph from a While statement
	 */
	private SubGraph process(CtWhile whileLoop){
		//Get data
		CtExpression<Boolean> condition = whileLoop.getLoopingExpression();
		CtUnaryOperator<Boolean> negCondition = factory.Core().createUnaryOperator();
		negCondition.setOperand(condition);
		negCondition.setKind(UnaryOperatorKind.NOT);
		//negCondition.setParent(condition);
		CtStatement body = whileLoop.getBody();
		
		//Build the sub-graph
		ConditionalSolution continueSolutions = Solver.solve(condition.toString());
		ConditionalSolution breakSolutions = Solver.solve("!("+condition.toString()+")");
		SubGraph loopBody = process(body);
		
		SubGraph res = new SubGraph(cfg);
		res.getEntry().addChild(loopBody.getEntry(),condition);
		res.getEntry().addChild(res.getExit(),negCondition);
		loopBody.getExit().addChild(loopBody.getEntry(),condition);
		loopBody.getExit().addChild(res.getExit(),negCondition);
		
		return res;
	}
	
	/**
	 * Build a control flow graph for an expression
	 */
	private SubGraph process(CtExpression<?> expression){
		List<CtCodeElement> current = new ArrayList<>();
		current.add(expression);
		SubGraph res = new SubGraph(current,cfg);
		
		return res;
	}
	
	/**
	 * Dispatch method
	 */
	private SubGraph process(CtStatement statement){
		
		if(statement instanceof CtConditional){
			return process((CtConditional<?>)statement);
		}
		else if(statement instanceof CtDo){
			return process((CtDo)statement);
		}
		else if(statement instanceof CtFor){
			return process((CtFor)statement);
		}
		else if(statement instanceof CtForEach){
			return process((CtForEach)statement);
		}
		else if(statement instanceof CtIf){
			return process((CtIf)statement);
		}
		else if(statement instanceof CtSwitch){
			return process((CtSwitch<?>)statement);
		}
		else if(statement instanceof CtTry){
			return process((CtTry)statement);
		}
		else if(statement instanceof CtWhile){
			return process((CtWhile)statement);
		}
		else if(statement instanceof CtBlock){
			return process((CtBlock<?>)statement);
		}
		else if(statement instanceof CtReturn){
			ArrayList<CtCodeElement> ret = new ArrayList<>();
			ret.add(statement);
			SubGraph res = new SubGraph(ret,cfg);
			res.setEntry(res.getExit());
			returnStatements.add(res);
			return res;
		}

		//Default case : build a basic block
		List<CtCodeElement> current = new ArrayList<>();
		current.add(statement);
		SubGraph res = new SubGraph(current,cfg);
		
		return res;
	}
	
	/**
	 * Build a control flow graph from a block
	 */
	private SubGraph process(CtBlock<?> block){	
		List<CtStatement> statements = block.getStatements();
		return process(statements);
	}
	
	/**
	 * Add the method in the control flow graph
	 */
	public static SubGraph build(CtExecutable<?> method, ControlFlowGraph cfg){
		
		CfgBuilder builder = new CfgBuilder(cfg);
		
		SubGraph res = new SubGraph(cfg);
		CtBlock<?> bodyStmt = method.getBody();
		if(bodyStmt != null){
			SubGraph body = builder.process(bodyStmt);
			res.setEntry(body.getEntry());
			body.getExit().addChild(res.getExit(), null);
		}
		else{
			res.setEntry(res.getExit());
		}
		
		
		for(SubGraph block : builder.returnStatements){
			block.getExit().setReturnChild(res.getExit());
		}
		
		return res;
	}
}
