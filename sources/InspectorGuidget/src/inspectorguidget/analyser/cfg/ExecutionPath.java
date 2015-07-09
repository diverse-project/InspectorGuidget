package inspectorguidget.analyser.cfg;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manage execution paths in a control flow graph
 */
public class ExecutionPath {
	
	List<SubGraph> path;
	
	
	public void resolve(int i){
		//TODO: retrieve instances
	}
	
	/**
	 * Get all executions paths from the control flow graph
	 */
	public static List<List<BasicBlock>> getPaths(SubGraph graph){
		return getPaths(graph.getEntry(), new HashSet<BasicBlock>());
	}
	
	/**
	 * Get all possibles paths from this node
	 */
	private static List<List<BasicBlock>> getPaths(BasicBlock node, Set<BasicBlock> visited){
		ArrayList<List<BasicBlock>> res = new ArrayList<>();
		
		List<BasicBlock> children = node.getChildren();
		for(BasicBlock child : children){
			if(!visited.contains(child)){
				visited.add(child);
				List<List<BasicBlock>> paths = getPaths(child,visited);
				for(List<BasicBlock> path : paths){
					path.add(0, child);
					res.add(path);
				}
				visited.remove(child);
			}
		}
		
		if(children.size() == 0){
			List<BasicBlock> leaf = new ArrayList<>();
			leaf.add(node);
			res.add(leaf);
		}
		
		return res;
	}

}
