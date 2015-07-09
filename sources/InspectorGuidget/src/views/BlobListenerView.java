package views;

import helper.FileHelper;
import inspectorguidgetplugin.Activator;
import inspectorguidgetplugin.popup.actions.BlobListeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

public class BlobListenerView extends ViewPart {

	private static BlobListenerView	INSTANCE;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String		ID			= "views.BlobListenerView";

	private List<IMarker>			markerList	= new ArrayList<>();

	private TableViewer				viewer;

	/**
	 * The constructor.
	 */
	public BlobListenerView() {
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

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Info", message);
	}

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

				String label = BlobListeners.getMethod((IMarker) element);
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
				String file = store.getString("pathBlobListeners");

				boolean checked = event.getChecked();

				IMarker marker = (IMarker) event.getElement();
				String info = BlobListeners.getInfo(marker);

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
			IEditorPart editor = IDE.openEditor(page, marker);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void init() {
		for (int i = getSingleton().markerList.size() - 1; i >= 0; i--)
			getSingleton().markerList.remove(i);
	}

	public static BlobListenerView getSingleton() {
		if (INSTANCE == null) {
			INSTANCE = new BlobListenerView();
		}
		return INSTANCE;
	}

	public static void addMarker(IMarker marker) {
		getSingleton().markerList.add(marker);

		int size = getSingleton().markerList.size();
		getSingleton().setPartName(size + " Blob listeners");

		getSingleton().viewer.refresh();

	}
}