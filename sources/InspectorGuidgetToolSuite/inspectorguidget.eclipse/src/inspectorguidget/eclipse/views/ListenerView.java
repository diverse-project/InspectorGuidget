package inspectorguidget.eclipse.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;

import inspectorguidget.eclipse.Activator;
import inspectorguidget.eclipse.actions.DetectGUIListenerAction;
import inspectorguidget.eclipse.helper.FileHelper;
import inspectorguidget.eclipse.preferences.PreferencePage;

public class ListenerView extends InspectorGuidgetView {
	private static ListenerView	INSTANCE;
	
	private static final String VIEW_TITLE = "GUI listeners";

	/** The ID of the view as specified by the extension. */
	public static final String ID = "inspectorguidget.eclipse.views.ListenerView";

	
	public ListenerView() {
		INSTANCE = this;
	}

	
	public static ListenerView getSingleton() {
		if(INSTANCE == null) {
			INSTANCE = new ListenerView();
		}
		return INSTANCE;
	}


	@Override
	public String getViewTitle() {
		return VIEW_TITLE;
	}


	@Override
	protected LabelProvider getLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(final Object element) {
				String label = DetectGUIListenerAction.getMethod((IMarker) element);
				if(label != null) return label;
				return element.toString();
			}
		};
	}


	@Override
	protected ICheckStateListener getCheckStateListener() {
		return event -> {
			String file = Activator.getDefault().getPreferenceStore().getString(PreferencePage.PATH_STORE);
			String info = DetectGUIListenerAction.getInfo((IMarker) event.getElement());
			FileHelper.appendFile(file, info + ";" + event.getChecked());
			// MessageDialog.openInformation( viewer.getControl().getShell(), "My new View", ""+event.getChecked());
		};
	}
}