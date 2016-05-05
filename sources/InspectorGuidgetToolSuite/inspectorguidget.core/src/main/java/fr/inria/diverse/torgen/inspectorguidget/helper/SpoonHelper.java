package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.eclipse.jdt.annotation.Nullable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtExecutable;

import java.util.Collections;
import java.util.List;

public final class SpoonHelper {
	public static final SpoonHelper INSTANCE = new SpoonHelper();

	private SpoonHelper() {
		super();
	}

	public String formatPosition(final @Nullable SourcePosition position) {
		if(position==null)
			return "";

		return "in " + position.getFile().getName()+":L"+position.getLine()+":"+position.getEndLine()
				+",C"+position.getColumn()+":"+position.getEndColumn();
	}

	public List<CtStatement> getConditionalStatements(final CtExecutable<?> exec) {
		return exec==null || exec.getBody()==null ? Collections.emptyList() : exec.getBody().getElements(new ConditionalFilter());
	}
}
