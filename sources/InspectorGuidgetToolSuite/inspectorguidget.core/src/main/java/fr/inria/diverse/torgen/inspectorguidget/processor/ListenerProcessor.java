package fr.inria.diverse.torgen.inspectorguidget.processor;

import fr.inria.diverse.torgen.inspectorguidget.helper.LoggingHelper;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public abstract class ListenerProcessor<T extends CtElement> extends InspectorGuidgetProcessor<T> {
	// Listener interfaces targeted
	protected List<CtTypeReference<?>> swingListenersRef;
	protected List<CtTypeReference<?>> awtListenersRef;
	protected List<CtTypeReference<?>> jfxListenersRef;
	//	List<CtTypeReference<?>> swtListenersRef;
	protected CtTypeReference<?> eventListenerRef;

	// Event interface
	protected CtTypeReference<?> eventRef;

	protected final Set<CtTypeReference<?>> events;


	public ListenerProcessor() {
		super();
		events = new HashSet<>();
	}

	@Override
	public void init() {
		if(LOG.isLoggable(Level.ALL))
			LOG.log(Level.INFO, "init processor " + getClass().getSimpleName());

		// Generic listener
		eventListenerRef = getFactory().Type().createReference(java.util.EventListener.class);

		jfxListenersRef = new ArrayList<>();
		jfxListenersRef.add(getFactory().Type().createReference(javafx.event.EventHandler.class));

		// Swing listeners
		swingListenersRef = new ArrayList<>();
		//		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.AncestorListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.CaretListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.CellEditorListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.ChangeListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.DocumentListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.HyperlinkListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.InternalFrameListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.ListDataListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.ListSelectionListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.MenuKeyListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.MenuListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.MouseInputListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.PopupMenuListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.RowSorterListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.TableColumnModelListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.TableModelListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.TreeExpansionListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.TreeModelListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.TreeSelectionListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.TreeWillExpandListener.class));
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.UndoableEditListener.class));
		// Added
		swingListenersRef.add(getFactory().Type().createReference(javax.swing.event.MenuDragMouseEvent.class));

		// Awt listeners
		awtListenersRef = new ArrayList<>();
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.ActionListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.AdjustmentListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.AWTEventListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.ComponentListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.ContainerListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.FocusListener.class));
		//		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.HierarchyBoundsListener.class));
		//		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.HierarchyListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.InputMethodListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.ItemListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.KeyListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.MouseListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.MouseMotionListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.MouseWheelListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.TextListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.WindowFocusListener.class));
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.WindowStateListener.class));
		// Added
		awtListenersRef.add(getFactory().Type().createReference(java.awt.event.WindowListener.class));

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

		eventRef = getFactory().Type().createReference(java.util.EventObject.class);
	}


	protected boolean isListenerCass(final CtTypeInformation type) {
		try {
			return type.isSubtypeOf(eventListenerRef);
		} catch(final Exception ex) {
			LoggingHelper.INSTANCE.logException(ex, LOG);
		}
		return false;
	}
}
