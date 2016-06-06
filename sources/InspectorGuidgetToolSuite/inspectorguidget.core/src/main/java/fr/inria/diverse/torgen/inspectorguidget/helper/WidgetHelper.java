package fr.inria.diverse.torgen.inspectorguidget.helper;

import org.jetbrains.annotations.NotNull;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WidgetHelper {
	public static final @NotNull WidgetHelper INSTANCE = new WidgetHelper();

	private List<CtTypeReference<?>> controlTypes;
	private Set<String> widgetPackages;

	private WidgetHelper() {
		super();
	}



	public @NotNull List<CtTypeReference<?>> getWidgetTypes(final @NotNull Factory factory) {
		if(controlTypes==null) {
			controlTypes = Arrays.asList(factory.Type().createReference(javafx.scene.Node.class),
										factory.Type().createReference(javafx.scene.control.MenuItem.class),
										factory.Type().createReference(javafx.scene.control.Dialog.class),
										factory.Type().createReference(javafx.stage.Window.class),
										factory.Type().createReference(java.awt.Component.class));
		}
		return controlTypes;
	}

	public @NotNull Set<String> getWidgetPackages() {
		if(widgetPackages==null) {
			widgetPackages = new HashSet<>();
			widgetPackages.add("javafx.scene");
			widgetPackages.add("javafx.stage");
			widgetPackages.add("java.awt");
			widgetPackages.add("javax.swing");
		}
		return widgetPackages;
	}

	public boolean isTypeRefAWidget(final @NotNull CtTypeReference<?> typeref) {
		return getWidgetTypes(typeref.getFactory()).stream().filter(type -> type==typeref || typeref.isSubtypeOf(type)).findFirst().isPresent();
	}

	public boolean isTypeRefAToolkitWidget(final @NotNull CtTypeReference<?> typeref) {
		final String qName = typeref.getQualifiedName();
		return getWidgetPackages().stream().filter(pkg -> qName.startsWith(pkg)).findFirst().isPresent();
	}
//
//
//	public boolean isTypeRefAToolkitWidget(final @NotNull CtTypeReference<?> typeref) {
//		return getWidgetTypes(typeref.getFactory()).stream().filter(type -> {
//			try {
//				return type == typeref || type.isSubtypeOf(typeref);
//			}
//			catch(Throwable ex) { }
//			return false;
//		}).findFirst().isPresent();
//	}
}
