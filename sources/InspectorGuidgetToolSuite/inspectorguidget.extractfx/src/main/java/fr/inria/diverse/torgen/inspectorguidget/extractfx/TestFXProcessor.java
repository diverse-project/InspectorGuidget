package fr.inria.diverse.torgen.inspectorguidget.extractfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testfx.api.FxRobotInterface;
import org.testfx.framework.junit.ApplicationTest;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.SpoonClassNotFoundException;

public class TestFXProcessor extends AbstractProcessor<CtMethod<?>> {
	private final @NotNull List<List<Cmd<?>>> exps = new ArrayList<>();

	@Override
	public boolean isToBeProcessed(final @NotNull CtMethod<?> method) {
		final CtClass<?> parentClass = method.getParent(CtClass.class);

		return parentClass != null && method.getVisibility() != ModifierKind.ABSTRACT &&
			method.getAnnotations().stream().anyMatch(anot -> "Test".equals(anot.getAnnotationType().getSimpleName())) &&
			parentClass.isSubtypeOf(getFactory().Type().createReference(ApplicationTest.class));
	}

	@Override
	public void process(final @NotNull CtMethod<?> method) {
		final List<CtInvocation<?>> assertions = getTestAssertions(method);

//		System.out.println(method.getReference().getDeclaringType().getSimpleName() + "::" + method.getSimpleName() + " " + assertions.size());
		exps.add(getTestConfigurationStatements(method, assertions).stream().map(stat -> {
			if(isRobotInvocation(stat)) {
				return Collections.singletonList(new RobotCmd(stat));
			}
			return getGUICommands(stat);
		}).flatMap(s -> s.stream()).collect(Collectors.toList()));

//		System.out.println(exps);
	}

	private @NotNull List<CtInvocation<?>> getTestAssertions(final @NotNull CtMethod<?> method) {
		return method.getBody().getElements(invok -> invok.getExecutable().getSimpleName().startsWith("assert"));
	}


	private @NotNull List<CtStatement> getTestConfigurationStatements(final @NotNull CtMethod<?> method, final @NotNull List<CtInvocation<?>> assertions) {
		final CtBlock<?> body = method.getBody();
		final int line = assertions.stream().mapToInt(assertion -> assertion.getPosition().getLine()).min().orElse(Integer.MIN_VALUE);
		final int bodyLine = body.getPosition().getLine();
		return body.getElements((CtStatement elt) -> elt.getPosition().getLine() > bodyLine && elt.getPosition().getLine() < line).stream().
			sorted((s1, s2) -> s1.getPosition().getLine() < s2.getPosition().getLine() ? -1 :
				s1.getPosition().getLine() > s2.getPosition().getLine() ? 1 :
				s1.getPosition().getColumn() < s2.getPosition().getColumn() ? 1 : -1).collect(Collectors.toList());
	}


	public static boolean isRobotInvocation(final @NotNull CtStatement stat) {
		if(!(stat instanceof CtInvocation<?>)) return false;

		final CtInvocation<?> invok = (CtInvocation<?>) stat;
		final CtTypeReference<Node> refNode = stat.getFactory().Type().createReference(Node.class);
		final CtTypeReference<FxRobotInterface> refRobot = stat.getFactory().Type().createReference(FxRobotInterface.class);

		return invok.getExecutable().getDeclaringType().isSubtypeOf(refRobot) || invok.getExecutable().getParameters().stream().anyMatch(arg -> {
			try {
				return arg.getTypeDeclaration().isSubtypeOf(refNode);
			}catch(final SpoonClassNotFoundException ex) {
				return false;
			}
		});
	}

	private @NotNull List<Cmd<?>> getGUICommands(final @NotNull CtStatement stat) {
		//TODO analyse setUp
		if(stat instanceof CtConstructorCall<?> && ((CtConstructorCall<?>) stat).getExecutable().getType().getSimpleName().equals("CompositeGUIVoidCommand")) {
			return ((CtConstructorCall<?>) stat).getArguments().stream().map(arg -> new GUICmd(arg)).collect(Collectors.toList());
//			return new ArrayList<>(((CtConstructorCall<?>) stat).getArguments());
		}

		if(stat instanceof CtInvocation<?> && isCmd(((CtInvocation<?>) stat).getExecutable().getDeclaringType())) {
			return Collections.singletonList(new GUICmd((CtExpression<?>)stat));
		}

		return Collections.emptyList();
	}

	private boolean isCmd(@Nullable CtTypeReference<?> type) {
		//FIXME use typeref
		return type != null && (type.getSimpleName().equals("GUIVoidCommand") || type.getSimpleName().equals("GUICommand")
//			||
//			type.getSuperInterfaces().stream().
//				anyMatch(inter -> inter.getSimpleName().equals("GUIVoidCommand") || inter.getSimpleName().equals("GUICommand"))
		);

	}

	public @NotNull List<List<Cmd<?>>> getExps() {
		return Collections.unmodifiableList(exps);
	}
}
