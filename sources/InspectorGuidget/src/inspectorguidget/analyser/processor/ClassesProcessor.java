package inspectorguidget.analyser.processor;

import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
/*
 * Process all classes in the source code
 */
public class ClassesProcessor extends AbstractProcessor<CtClass<?>>
{
	final List<CtClass<?>> allClasses;
	
	public ClassesProcessor(List<CtClass<?>> classes){
		allClasses = classes;
	}

	@Override
	public void process(CtClass<?> clazz) {
		allClasses.add(clazz);
	}
}
