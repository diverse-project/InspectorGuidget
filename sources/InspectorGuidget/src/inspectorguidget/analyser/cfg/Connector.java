package inspectorguidget.analyser.cfg;

import spoon.reflect.code.CtExpression;

/**
 * Represent an empty block
 */
public class Connector extends BasicBlock{
	
	public Connector(ControlFlowGraph cfg) {
		super(cfg);
	}
	
	/**
	 * Print empty circle node in dot format
	 * and its outgoing edges
	 */
	public String toString(){
		StringBuilder res = new StringBuilder();
		
		res.append(this.hashCode()+"[label=\"");

		res.append("\"]\n");
		
		for(BasicBlock child : children){
			res.append(this.hashCode()+"->"+child.hashCode());
			 CtExpression cond = this.getCondition(child);
			if(cond != null){
				res.append("[label=\""+ cond.toString().replaceAll("\\u0022", "\\\\\"").replaceAll("\n", "\\\\l") +"\"]\n");
			}
			else{
				res.append("\n");
			}
		}
		
		return res.toString();
	}
}
