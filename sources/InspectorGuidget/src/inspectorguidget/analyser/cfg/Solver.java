package inspectorguidget.analyser.cfg;

import java.util.ArrayList;

import java.util.List;

import inspectorguidget.analyser.Pair;

public class Solver {
	
	/**
	 * Get solutions for a Java expression
	 */
	public static ConditionalSolution solve(String expression){
		//TODO: use an external library
		List<List<Pair<String,Boolean>>> solutions = new ArrayList<List<Pair<String,Boolean>>>();
		List<Pair<String,Boolean>> dummySolution = new ArrayList<Pair<String,Boolean>>();
		dummySolution.add(new Pair<String, Boolean>(expression, true));
		ConditionalSolution res = new ConditionalSolution(expression,solutions);
		return res;
	}

}
