package inspectorguidget.analyser;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

/*
 * Get the component and event types for Swing and AWT and
 * the method calls of listener registrations
 */
public class ListenerType {
	CtTypeReference<Component>		awtComponentRef;
	CtTypeReference<JComponent>		swingComponentRef;
	CtTypeReference<EventListener>	eventListenerRef;
	CtTypeReference<EventObject>	eventRef;
	List<CtTypeReference<?>>		swingListeners;
	List<CtTypeReference<?>>		awtListeners;
	List<CtTypeReference<?>>		adapterListeners;
	List<String>					registers;
	Factory							fac;

	public ListenerType(Factory factory) {
		this.awtComponentRef = factory.Type().createReference(java.awt.Component.class);
		this.swingComponentRef = factory.Type().createReference(javax.swing.JComponent.class);// javax.swing.tree.TreePath?
		this.eventRef = factory.Type().createReference(java.util.EventObject.class);
		this.eventListenerRef = factory.Type().createReference(java.util.EventListener.class);
		this.fac = factory;

		// Swing listeners
		swingListeners = new ArrayList<>();
		swingListeners.add(factory.Type().createReference(javax.swing.event.AncestorListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.CaretListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.CellEditorListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.ChangeListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.DocumentListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.HyperlinkListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.InternalFrameListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.ListDataListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.ListSelectionListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.MenuKeyListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.MenuListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.MouseInputListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.PopupMenuListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.RowSorterListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.TableColumnModelListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.TableModelListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.TreeExpansionListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.TreeModelListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.TreeSelectionListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.TreeWillExpandListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.UndoableEditListener.class));
		swingListeners.add(factory.Type().createReference(javax.swing.event.MenuDragMouseEvent.class));

		// Awt listeners
		awtListeners = new ArrayList<>();
		awtListeners.add(factory.Type().createReference(java.awt.event.ActionListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.AdjustmentListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.AWTEventListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.ComponentListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.ContainerListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.FocusListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.HierarchyBoundsListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.HierarchyListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.InputMethodListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.ItemListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.KeyListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.MouseListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.MouseMotionListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.MouseWheelListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.TextListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.WindowFocusListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.WindowStateListener.class));
		awtListeners.add(factory.Type().createReference(java.awt.event.WindowListener.class));

		// Adapter classes
		adapterListeners = new ArrayList<>();
		adapterListeners.add(factory.Type().createReference(java.awt.event.ComponentAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.ContainerAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.FocusAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.HierarchyBoundsAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.KeyAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.MouseAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.MouseMotionAdapter.class));
		adapterListeners.add(factory.Type().createReference(java.awt.event.WindowAdapter.class));

		adapterListeners.add(factory.Type().createReference(javax.swing.event.MouseInputAdapter.class));
		adapterListeners.add(factory.Type().createReference(javax.swing.event.InternalFrameAdapter.class));

		// TODO: check spinner model: extends JComponent
	}

	public ListenerType() {
		// References for a listener registration
		registers = new ArrayList<>();
		registers.add("addComponentListener");
		registers.add("addFocusListener");
		registers.add("addHierarchyBoundsListener");
		registers.add("addHierarchyListener");
		registers.add("addInputMethodListener");
		registers.add("addKeyListener");
		registers.add("addMouseListener");
		registers.add("addMouseMotionListener");
		registers.add("addMouseWheelListener");
		registers.add("addActionListener");
		registers.add("addItemListener");
		registers.add("addContainerListener");
		registers.add("addAdjustmentListener");
		registers.add("addTextListener");
		registers.add("addCaretListener");
		registers.add("addChangeListener");
		registers.add("addDocumentListener");
		registers.add("addInternalFrameListener");
		registers.add("addListDataListener");
		registers.add("addListSelectionListener");
		registers.add("addPropertyChangeListener");
		registers.add("addTableModelListener");
		registers.add("addTreeExpansionListener");
		registers.add("addTreeModelListener");
		registers.add("addTreeSelectionListener");
		registers.add("addTreeWillExpandListener");
		registers.add("addUndoableEditListener");
		registers.add("addWindowListener");
		registers.add("addWindowFocusListener");
		registers.add("addWindowStateListener");
		registers.add("addAncestorListener");
		registers.add("addCellEditorListener");
		registers.add("addExceptionListener");
		registers.add("addHyperlinkListener");
		registers.add("addMenuDragMouseListener");
		registers.add("addMenuKeyListener");
		registers.add("addMenuListener");
		registers.add("addMouseInputListener");// ?
		registers.add("addPopupMenuListener");
		registers.add("addColumnModelListener");
		registers.add("addVetoableChangeListener");
		// registers.addLabeledSpinner("addLabeledSpinner");
	}

	public boolean isComponent(CtTypeReference<?> comp) {
		CtTypeReference<?> type = getTypeReference(comp);
		if (type != null) {
			if (type.isSubtypeOf(awtComponentRef)) {
				return true;
			} else if (type.isSubtypeOf(swingComponentRef)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEventRef(CtTypeReference<?> event) {
		CtTypeReference<?> type = getTypeReference(event);

		if (type != null) {
			if (type.isSubtypeOf(eventRef)) {
				return true;
			} else if (type.isSubtypeOf(eventListenerRef)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsEventRef(Set<CtVariable<?>> vars) {
		for (CtVariable<?> var : vars) {
			if (var.getType().isSubtypeOf(eventRef)) {
				return true;
			} else if (var.getType().isSubtypeOf(eventListenerRef)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsComponentType(Set<CtVariable<?>> vars) {
		for (CtVariable<?> var : vars) {
			if (var.getType().isSubtypeOf(awtComponentRef)) {
				return true;
			} else if (var.getType().isSubtypeOf(swingComponentRef)) {
				return true;
			}
		}
		return false;
	}

	public boolean isComponentRef(CtExpression<?> expr) {
		// CtTypeReference type2 = expr.getType();
		CtTypeReference<?> typeRef = getExprReference(expr);
		// CtTypeReference<?> type = getTypeReference(typeRef);//Check if this
		// will be needed
		if (typeRef != null) {

			if (typeRef.isSubtypeOf(awtComponentRef)) {
				return true;
			} else if (typeRef.isSubtypeOf(swingComponentRef)) {
				return true;
			}

		}
		return false;
	}

	private CtTypeReference<?> getExprReference(CtExpression<?> expr) {
		if (expr instanceof CtLiteral) {
			CtLiteral<?> literal = (CtLiteral<?>) expr;
			if (literal.getValue() instanceof CtTypeReference) {// Workaround
																// for exprs
																// such as -1
				CtTypeReference<?> type = (CtTypeReference<?>) literal.getValue();
				if (type.getDeclaration() != null) {
					return type.getDeclaration().getReference();
				} else if (type.getDeclaringType() != null) {
					return type.getDeclaringType();
				} else {
					return fac.Type().createReference(expr.toString());
				}
			}
		}
		return null;
	}

	/*
	 * Get the correctly type when a type is array: class[]
	 */
	public CtTypeReference<?> getTypeReference(CtTypeReference<?> type) {
		if (type instanceof CtArrayTypeReference) {
			CtArrayTypeReference<?> arrayType = (CtArrayTypeReference<?>) type;
			return arrayType.getComponentType();
		}
		return type;
	}

	public List<CtTypeReference<?>> getSwingListenersRef() {
		return swingListeners;
	}

	public List<CtTypeReference<?>> getAWTListenersRef() {
		return awtListeners;
	}

	public List<String> getCallOfRegistrations() {
		return registers;
	}

	public boolean isListenerRegistration(String call) {
		if (registers.contains(call)) {
			return true;
		}
		return false;
	}

	// /**
	// * Return true if the declaration of the type is in the analyzed source
	// code
	// */
	// private boolean isDeclaredInsourceCode(CtTypeReference<?> type){
	// if(type != null) {
	// CtType<?> dec = type.getDeclaration();
	// if (dec != null){
	// return true;
	// }
	// }
	// return false;
	// }
}
