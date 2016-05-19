package inspectorguidget.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

public abstract class InspectorGuidgetView extends ViewPart {
	protected List<IMarker> markerList = new ArrayList<>();

	protected TableViewer viewer;
	
	
	public InspectorGuidgetView() {
		super();
	}
	

	/** This is a callback that will allow us to create the viewer and initialize it. */
	@Override
	public void createPartControl(final Composite parent) {
		viewer = makeTable(parent);
	}


	/** Passing the focus request to the viewer's control. */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	protected abstract LabelProvider getLabelProvider();
	
	protected abstract ICheckStateListener getCheckStateListener();
	
	protected TableViewer makeTable(final Composite parent) {
		CheckboxTableViewer tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(markerList);

		tableViewer.setLabelProvider(getLabelProvider());

		tableViewer.addSelectionChangedListener(event -> {
			Object marker = ((StructuredSelection) event.getSelection()).getFirstElement();
			if(marker instanceof IMarker) {
				openEditor((IMarker) marker);
			}
		});

		tableViewer.addCheckStateListener(getCheckStateListener());

		return tableViewer;
	}
	
	
	protected void openEditor(final IMarker marker) {
		try {
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	
	public void clearMarkers() {
		markerList.forEach(marker -> {
			try {
				marker.delete();
			}catch(Exception e) {
				e.printStackTrace();
			}
		});
		markerList.clear();
		if(viewer!=null)
			viewer.refresh();
		setPartName(getViewTitle());
	}
	
	
	public void addMarker(final IMarker marker) {
		markerList.add(marker);
		int size = markerList.size();
		setPartName(size + " " + getViewTitle());
		if(viewer!=null)
			viewer.refresh();
	}
	
	public abstract String getViewTitle();
}
