package inspectorguidget.analyser.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;

/**
 * Represent a list of non conditional statements
 */
public class BasicBlock{
	
	/**
	 * Container of this block
	 */
	ControlFlowGraph cfg;
	
	/**
	 * Content of the block
	 */
	List<CtCodeElement> statements;

	/**
	 * Conditionals edges
	 */
	Map<BasicBlock,CtExpression<?>> conditions;
	List<BasicBlock> children;
	List<BasicBlock> parents;
	
	/**
	 * Create a BasicBlock in this cfg
	 */
	public BasicBlock(ControlFlowGraph cfg) {
		statements = new ArrayList<>();
		children = new ArrayList<>();
		parents = new ArrayList<>();
		conditions = new HashMap<>();
		this.cfg = cfg;
		cfg.addNode(this);
	}
	
	/**
	 * Create a BasicBlock in this cfg with those statements
	 */
	public BasicBlock(List<CtCodeElement> body, ControlFlowGraph cfg) {
		statements = body;
		children = new ArrayList<>();
		parents = new ArrayList<>();
		conditions = new HashMap<>();
		this.cfg = cfg;
		cfg.addNode(this);
	}
	
	public List<CtCodeElement> getElements(){
		return statements;
	}
	
	public void addChild(BasicBlock node, CtExpression<?> cond){
		children.add(node);
		conditions.put(node, cond);
		node.getParents().add(this);
	}
	
	public void removeChild(BasicBlock node){
		//TODO: check node
		node.parents.remove(this);
		children.remove(node);
		conditions.remove(node);
	}
	
	/**
	 * Remove all children and set only one child
	 */
	public void setReturnChild(BasicBlock returnNode){
		for(BasicBlock child : children){
			child.parents.remove(this);
			conditions.remove(child);
		}
		this.children = new ArrayList<>();
		this.addChild(returnNode, null); //"return"
	}
	
	public void removeParent(BasicBlock node){
		//TODO:check node
		parents.remove(node);
		node.children.remove(this);
		node.conditions.remove(this);
	}
	
	public List<BasicBlock> getChildren(){
		return children;
	}
	
	public List<BasicBlock> getParents(){
		return parents;
	}
	
	public CtExpression<?> getCondition(BasicBlock child){
		//TODO: check child is a node's child
		return conditions.get(child);
	}
	
	public void merge(BasicBlock node){
		this.addChild(node, null);
		//TODO:node should be a child
		 this.removeChild(node);
		 
		 List<BasicBlock> toRemove = new ArrayList<>();
		 for(BasicBlock parent : node.getParents()){
			 parent.addChild(this, parent.getCondition(node));
			 toRemove.add(parent);
		 }
		 for(BasicBlock parent : toRemove){
			 node.removeParent(parent);
		 }
		 
		 toRemove = new ArrayList<>();
		 for(BasicBlock child : node.getChildren()){
			 this.addChild(child, node.getCondition(child));
			 toRemove.add(child);
		 }
		 node.children.removeAll(toRemove);
		 cfg.removeNode(node);
	}
	
	@Override
	public String toString(){
		StringBuilder res = new StringBuilder();
		
		res.append(this.hashCode()+"[id="+this.getID()+"][shape=box][label=\"");
		
		for(CtCodeElement line : statements){
			res.append(line.toString().replaceAll("\\u0022", "\\\\\"").replaceAll("\n", "\\\\l")+"\\l");
		}
		res.append("\"]\n");

		for(BasicBlock child : children){
			res.append(this.hashCode()+"->"+child.hashCode());
			CtExpression<?> cond = this.getCondition(child);
			if(cond != null){
				res.append("[label=\""+ cond.toString().replaceAll("\\u0022", "X").replaceAll("\n", "\\\\l") +"\"]\n");
			}
			else{
				res.append("\n");
			}
		}
		
		return res.toString();
	}
	
	/**
	 * Compute an ID from hashcode of this block and from its parents
	 */
	public String getID(){
		StringBuffer res = new StringBuffer();
		res.append(this.hashCode());
		for(BasicBlock parent : getParents()){
			res.append(parent.hashCode());
		}
		
		return res.toString();
	}
}
