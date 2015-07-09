package inspectorguidget.analyser.designsmells;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;

import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import inspectorguidget.analyser.dataflow.Action;

public class BlobListener {
	
	IdentityHashMap<CtMethod, List<Action>> blobListeners;
	//ListenerRegistrationsWrapper regWrapper;
	
	public BlobListener(List<Action> commands){
		blobListeners = new IdentityHashMap<CtMethod, List<Action>>();
		//blobListeners.putAll(findBlobListeners(commands));
		blobListeners = findBlobListeners(commands);
		System.out.println("#BlobListeners " + blobListeners.size());
	}
	
	
	//Look at TerPaint: it is not a good strategy
	public IdentityHashMap<CtMethod, List<Action>> findBlobListeners(List<Action> cmds){
		IdentityHashMap<CtMethod, List<Action>> candidatesBlob = gatherCommandsBySource(cmds);
		IdentityHashMap<CtMethod,List<Action>> res = new IdentityHashMap<CtMethod, List<Action>>();;
		
//		int count = 0;
		for (Entry<CtMethod, List<Action>> cmds2Listener : candidatesBlob.entrySet()){
			if (cmds2Listener.getValue().size() > 1){//There is more than one command per listener
				//if (isBlobListener(cmds2Listener.getKey())){
					res.put(cmds2Listener.getKey(), cmds2Listener.getValue());
				//}
				//count = count + 1;
			}
		}
		//System.out.println("#CandidatesBlob " + count);
		return res;
	}
	
	/*
	 * Gather commands that are detected in the same method
	 */
	private IdentityHashMap<CtMethod,List<Action>> gatherCommandsBySource(List<Action> commands){		
		IdentityHashMap<CtMethod, List<Action>> res = new IdentityHashMap<CtMethod, List<Action>>();	
		
		for (Action cmd : commands){
			List<Action> cmds = res.get(cmd.getSource());
			if (cmds == null ){
				cmds = new ArrayList<Action>();
				cmds.add(cmd);
				res.put(cmd.getSource(), cmds);
			}
			else{
				cmds.add(cmd);
			}
		}
		return res;	
	}
	
	//Look at TerPaint: it is not a good strategy
	public boolean isBlobListener(CtMethod source){
		CtElement parent = source.getDeclaringType().getParent();
		if (parent instanceof CtNewClass){
			return false;
		}	
		return true;
	}	
	
	public IdentityHashMap<CtMethod,List<Action>> getBlobListeners(){
		return blobListeners;
	}
}
