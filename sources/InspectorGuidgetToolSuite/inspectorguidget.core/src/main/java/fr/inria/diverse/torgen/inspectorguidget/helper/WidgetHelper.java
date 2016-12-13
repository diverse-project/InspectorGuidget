package fr.inria.diverse.torgen.inspectorguidget.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.reference.SpoonClassNotFoundException;

public final class WidgetHelper {
	public static final @NotNull WidgetHelper INSTANCE = new WidgetHelper();

	public final @NotNull List<String> ACTION_CMD_METHOD_NAMES = Collections.singletonList("setActionCommand");

	private List<CtTypeReference<?>> controlTypes;
	private Set<String> widgetPackages;
	private List<CtTypeReference<?>> swingListenersRef;
	private List<CtTypeReference<?>> awtListenersRef;
	private List<CtTypeReference<?>> jfxListenersRef;
	private CtTypeReference<?> eventListenerRef;
	private Map<String, CtExecutable<?>> listenerMethodPrototypes;
	private CtTypeReference<?> actionRef;

	private final Object LOCK = new Object();

	private WidgetHelper() {
		super();
	}

	public CtTypeReference<?> getActionRef(final @NotNull Factory factory) {
		synchronized(LOCK) {
			if(actionRef == null) {
				actionRef = factory.Type().createReference(javax.swing.AbstractAction.class);
			}
			return actionRef;
		}
	}

	public List<CtTypeReference<?>> getSwingListenersRef(final @NotNull Factory factory) {
		synchronized(LOCK) {
			if(swingListenersRef == null) {
				swingListenersRef = new ArrayList<>();
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.CaretListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.CellEditorListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.ChangeListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.DocumentListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.HyperlinkListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.InternalFrameListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.ListDataListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.ListSelectionListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.MenuKeyListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.MenuListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.MouseInputListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.PopupMenuListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.RowSorterListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.TableColumnModelListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.TableModelListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.TreeExpansionListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.TreeModelListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.TreeSelectionListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.TreeWillExpandListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.UndoableEditListener.class));
				swingListenersRef.add(factory.Type().createReference(javax.swing.event.MenuDragMouseEvent.class));
				registerListenerMethodsSignatures(swingListenersRef);
			}
		}
		return swingListenersRef;
	}

	private void registerListenerMethodsSignatures(final @NotNull List<CtTypeReference<?>> types) {
		synchronized(LOCK) {
			if(listenerMethodPrototypes == null) listenerMethodPrototypes = new HashMap<>();

			listenerMethodPrototypes.putAll(types.stream().map(typ -> typ.getDeclaredExecutables()).flatMap(s -> s.stream()).collect(Collectors.
				toMap(exec -> exec.getExecutableDeclaration().getSignature(), exec -> exec.getExecutableDeclaration())));
		}
	}

	public List<CtTypeReference<?>> getAWTListenersRef(final @NotNull Factory factory) {
		synchronized(LOCK) {
			if(awtListenersRef == null) {
				awtListenersRef = new ArrayList<>();
				awtListenersRef.add(factory.Type().createReference(java.awt.event.ActionListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.AdjustmentListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.AWTEventListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.ComponentListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.ContainerListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.FocusListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.InputMethodListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.ItemListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.KeyListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.MouseListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.MouseMotionListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.MouseWheelListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.TextListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.WindowFocusListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.WindowStateListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.event.WindowListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.dnd.DragGestureListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.dnd.DragSourceListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.dnd.DragSourceMotionListener.class));
				awtListenersRef.add(factory.Type().createReference(java.awt.dnd.DropTargetListener.class));
				registerListenerMethodsSignatures(awtListenersRef);
			}
		}
		return awtListenersRef;
	}

	public List<CtTypeReference<?>> getJFXListenersRef(final @NotNull Factory factory) {
		synchronized(LOCK) {
			if(jfxListenersRef == null) {
				jfxListenersRef = new ArrayList<>();
				jfxListenersRef.add(factory.Type().createReference(javafx.event.EventHandler.class));
				registerListenerMethodsSignatures(jfxListenersRef);
			}
		}
		return jfxListenersRef;
	}

	//SWT
	//		swtListenersRef = new ArrayList<>();
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleActionListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleAttributeListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleControlListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleEditableTextListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleHyperlinkListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleTableCellListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleTableListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleTextExtendedListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleTextListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.accessibility.AccessibleValueListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.ArmListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.AuthenticationListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.BidiSegmentListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.CaretListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.CloseWindowListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.ControlListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.CTabFolder2Listener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.CTabFolderListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.DisposeListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.DragDetectListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.dnd.DragSourceListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.dnd.DropTargetListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.ExpandListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.ExtendedModifyListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.FocusListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.GestureListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.HelpListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.graphics.ImageLoaderListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.KeyListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.LineBackgroundListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.LineStyleListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.LocationListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.MenuDetectListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.MenuListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.ModifyListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.MouseListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.MouseMoveListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.MouseTrackListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.MouseWheelListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.MovementListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.OpenWindowListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.PaintListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.PaintObjectListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.ProgressListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.SegmentListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.SelectionListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.ShellListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.StatusTextListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.TextChangeListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.TitleListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.TouchListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.jface.util.TransferDragSourceListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.jface.util.TransferDropTargetListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.TraverseListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.TreeListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.custom.VerifyKeyListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.events.VerifyListener.class));
	//		swtListenersRef.add(getFactory().Type().createReference(org.eclipse.swt.browser.VisibilityWindowListener.class));


	/**
	 * Looks for the listener interface that implements the given executable.
	 * @param exec The executable to search in the interfaces.
	 * @return The found interface or nothing. Cannot be null.
	 */
	public @NotNull Optional<CtType<?>> getListenerInterface(@Nullable CtExecutable<?> exec) {
		if(exec == null) {
			return Optional.empty();
		}

		CtExecutable<?> listenerExec = listenerMethodPrototypes.get(exec.getSignature());

		if(listenerExec == null) {
			return Optional.empty();
		}

		return Optional.of(listenerExec.getReference().getDeclaringType().getDeclaration());
	}


	public boolean isListenerClassMethod(final @NotNull CtExecutable<?> exec) {
		return isListenerClass(exec.getReference().getDeclaringType(), exec.getFactory(), null) && (exec instanceof CtLambda<?> ||
			listenerMethodPrototypes.get(exec.getSignature()) != null);
	}

	/**
	 * Checks whether the given type is a listener.
	 * @param type The type to check.
	 * @param factory The Spoon factory to use.
	 * @param ofType The type that 'type" must conform to. May be null.
	 * @return True if the given type is a listener (and conforms the given 'ofType'). False otherwise.
	 */
	public boolean isListenerClass(final @Nullable CtTypeInformation type, final @NotNull Factory factory, final @Nullable CtType<?> ofType) {
		synchronized(LOCK) {
			if(eventListenerRef == null && type != null) eventListenerRef = factory.Type().createReference(java.util.EventListener.class);
		}
		return type != null && type.isSubtypeOf(eventListenerRef) && !type.isSubtypeOf(getActionRef(factory)) && (ofType==null || type.equals(ofType.getReference()));
	}

	public @NotNull List<CtTypeReference<?>> getWidgetTypes(final @NotNull Factory factory) {
		synchronized(LOCK) {
			if(controlTypes == null) {
				controlTypes = Arrays.asList(factory.Type().createReference(javafx.scene.Node.class),
					factory.Type().createReference(javafx.scene.control.MenuItem.class),
					factory.Type().createReference(javafx.scene.control.Dialog.class),
					factory.Type().createReference(javafx.stage.Window.class),
					factory.Type().createReference(java.awt.Component.class));
			}
		}
		return controlTypes;
	}

	public @NotNull Set<String> getWidgetPackages() {
		synchronized(LOCK) {
			if(widgetPackages == null) {
				widgetPackages = new HashSet<>();
				widgetPackages.add("javafx.scene");
				widgetPackages.add("javafx.stage");
				widgetPackages.add("java.awt");
				widgetPackages.add("javax.swing");
			}
		}
		return widgetPackages;
	}

	public boolean isTypeRefAWidget(final @NotNull CtTypeReference<?> typeref) {
		try {
			return getWidgetTypes(typeref.getFactory()).stream().anyMatch(type -> type == typeref || typeref.isSubtypeOf(type));
		}catch(final SpoonClassNotFoundException ex) {
			return false;
		}
	}

	public boolean isTypeRefAToolkitWidget(final @NotNull CtTypeReference<?> typeref) {
		final String qName = typeref.getQualifiedName();
		return getWidgetPackages().stream().anyMatch(pkg -> qName.startsWith(pkg));
	}
}
