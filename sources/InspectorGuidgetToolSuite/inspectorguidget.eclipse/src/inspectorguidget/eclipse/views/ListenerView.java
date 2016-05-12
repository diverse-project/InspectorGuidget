package inspectorguidget.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import inspectorguidget.eclipse.Activator;
import inspectorguidget.eclipse.actions.DetectGUIListenerAction;
import inspectorguidget.eclipse.helper.FileHelper;

public class ListenerView extends ViewPart {

	// final static String FILE = "CheckedListeners.txt";
	final static String			FILE		= "/home/foo/Bureau/CheckedListeners.txt";

	private static ListenerView	INSTANCE;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String	ID			= "inspectorguidget.eclipse.views.ListenerView";

	private List<IMarker>		markerList	= new ArrayList<>();

	private TableViewer			viewer;

	/**
	 * The constructor.
	 */
	public ListenerView() {
		INSTANCE = this;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = makeTable(parent);
	}

//	private void showMessage(String message) {
//		MessageDialog.openInformation(viewer.getControl().getShell(), "Info", message);
//	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private TableViewer makeTable(Composite parent) {
		// CheckboxTableViewer tableViewer = new CheckboxTableViewer(parent,
		// SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		CheckboxTableViewer tableViewer;
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(markerList);

		tableViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				String label = DetectGUIListenerAction.getMethod((IMarker) element);
				if (label != null)
					return label;

				return element.toString();
			}
		});

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				IMarker marker = (IMarker) selection.getFirstElement();
				if (marker != null) {
					openEditor(marker);
				}
			}
		});

		tableViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {

				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				String file = store.getString("pathListeners");

				boolean checked = event.getChecked();

				IMarker marker = (IMarker) event.getElement();
				String info = DetectGUIListenerAction.getInfo(marker);

				FileHelper.appendFile(file, info + ";" + checked);

				// MessageDialog.openInformation(
				// viewer.getControl().getShell(),
				// "My new View",
				// ""+event.getChecked());
			}
		});

		return tableViewer;
	}

	private void openEditor(IMarker marker) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try {
			IDE.openEditor(page, marker);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		for (int i = getSingleton().markerList.size() - 1; i >= 0; i--)
			getSingleton().markerList.remove(i);
	}

	public static ListenerView getSingleton() {
		if (INSTANCE == null) {
			INSTANCE = new ListenerView();
		}
		return INSTANCE;
	}

	public static void addMarker(IMarker marker) {
		getSingleton().markerList.add(marker);

		int size = getSingleton().markerList.size();
		getSingleton().setPartName(size + " GUI listeners");

		getSingleton().viewer.refresh();

	}
}