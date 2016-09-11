package inspectorguidget.eclipse.actions;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ClearMarkersAction implements IObjectActionDelegate {
	public static String INSPECTOR_MARKER_NAME = "inspectorguidgetMarker";
	
	@Override
	public void run(final IAction action) {
		DetectGUIListenerAction.clearMarkers();
		DetectBlobListenerAction.clearMarkers();
		DetectGUIBugsAction.clearMarkers();
		DetectGUICommandAction.clearMarkers();
		clearInspectorGuidgetMarkers(AbstractAction.getCurrentProject());
	}

	public void clearInspectorGuidgetMarkers(IResource target) {
		if(target!=null)
			try {
				Arrays.stream(target.findMarkers(INSPECTOR_MARKER_NAME, true, IResource.DEPTH_INFINITE)).forEach(marker -> {
					try { marker.delete(); }
					catch(Exception e) { e.printStackTrace(); }	
				});
			}catch(CoreException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
	}

	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
	}
}
