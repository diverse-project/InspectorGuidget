package fr.inria.diverse.torgen.inspectorguidget.helper;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

import java.util.stream.IntStream;

public class SpoonStructurePrinter extends CtScanner {
	int nbTab = 0;
	final char tab = '\t';

	private void printTabs() {
		IntStream.range(0, nbTab).forEach(i -> System.out.print(tab));
	}

	@Override
	protected void enter(CtElement e) {
		super.enter(e);
		System.out.print('\n');
		printTabs();
		System.out.print(e.getClass().getSimpleName());

		if(e instanceof CtNamedElement) {
			System.out.print(": " + ((CtNamedElement)e).getSimpleName());
		}else if(e instanceof CtReference) {
			System.out.print(": " + ((CtReference)e).getSimpleName());
		}

		System.out.print(" " + e.toString());

		nbTab++;
	}

	@Override
	protected void exit(CtElement e) {
		super.exit(e);
		nbTab--;
	}
}
