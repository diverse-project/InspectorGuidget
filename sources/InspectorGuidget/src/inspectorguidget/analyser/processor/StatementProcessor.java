package inspectorguidget.analyser.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;



//public class StatementProcessor extends AbstractProcessor<CtStatement>{
public class StatementProcessor extends AbstractProcessor<CtStatement>{	
	int counter;

	@Override
	public void process(CtStatement arg0) {
		if (arg0.getParent() instanceof CtBlock){//Count the logical lines of code
			counter++;
		}		
	}
	
	@Override
	public void init() {
		counter = 0;
	}
	
	@Override
	public void processingDone() {
		
		super.processingDone();
	}
	
	public int getCount(){
		return counter;
	}

}
