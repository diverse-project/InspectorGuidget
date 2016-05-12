package inspectorguidget.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import inspectorguidget.eclipse.Activator;
import inspectorguidget.eclipse.actions.DetectGUIListenerAction;
import inspectorguidget.eclipse.helper.FileHelper;

public class ListenerView extends ViewPart {
	private static ListenerView	INSTANCE;

	/** The ID of the view as specified by the extension. */
	public static final String ID = "inspectorguidget.eclipse.views.ListenerView";

	private List<IMarker> markerList = new ArrayList<>();

	private TableViewer viewer;

	
	public ListenerView() {
		INSTANCE = this;
	}

	/** This is a callback that will allow us to create the viewer and initialize it. */
	@Override
	public void createPartControl(final Composite parent) {
		viewer = makeTable(parent);
	}

//	private void showMessage(String message) {
//		MessageDialog.openInformation(viewer.getControl().getShell(), "Info", message);
//	}

	/** Passing the focus request to the viewer's control. */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	
	private TableViewer makeTable(final Composite parent) {
		// CheckboxTableViewer tableViewer = new CheckboxTableViewer(parent,
		// SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		CheckboxTableViewer tableViewer;
		tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(markerList);

		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				String label = DetectGUIListenerAction.getMethod((IMarker) element);
				if(label != null) return label;
				return element.toString();
			}
		});

		tableViewer.addSelectionChangedListener(event -> {
			Object marker = ((StructuredSelection) event.getSelection()).getFirstElement();
			if(marker instanceof IMarker) {
				openEditor((IMarker) marker);
			}
		});

		tableViewer.addCheckStateListener(event -> {
			String file = Activator.getDefault().getPreferenceStore().getString("pathListeners");
			boolean checked = event.getChecked();
			String info = DetectGUIListenerAction.getInfo((IMarker) event.getElement());

			FileHelper.appendFile(file, info + ";" + checked);

			// MessageDialog.openInformation(
			// viewer.getControl().getShell(),
			// "My new View",
			// ""+event.getChecked());
		});

		return tableViewer;
	}

	private void openEditor(final IMarker marker) {
		try {
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}


	public static ListenerView getSingleton() {
		if (INSTANCE == null) {
			INSTANCE = new ListenerView();
		}
		return INSTANCE;
	}

	public void addMarker(final IMarker marker) {
		markerList.add(marker);
		int size = markerList.size();
		setPartName(size + " GUI listeners");
		viewer.refresh();
	}
}