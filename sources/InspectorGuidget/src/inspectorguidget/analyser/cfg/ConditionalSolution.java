package inspectorguidget.analyser.cfg;

import inspectorguidget.analyser.Pair;

import java.util.List;

/**
 * This class store all solutions from a solved expression
 */
public class ConditionalSolution {
	
	/*
	 * Original expression
	 */
	String expression;
	
	/*
	 * List of solutions for the expression
	 */
	List<List<Pair<String,Boolean>>> solutions;

	
	public ConditionalSolution(String expression, List<List<Pair<String,Boolean>>> solutions){
		this.expression = expression;
		this.solutions = solutions;
	}
	
	public int getNumberOfSolutions(){
		return solutions.size();
	}
	
	public List<Pair<String,Boolean>> getSolution(int index){
		return solutions.get(index);
	}
}
