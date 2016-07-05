package inspectorguidget.eclipse.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;

import inspectorguidget.eclipse.actions.DetectGUIBugsAction;

public class GUIBugsView extends InspectorGuidgetView {
	private static GUIBugsView	INSTANCE;
	
	private static final String VIEW_TITLE = "GUI Bugs";

	/** The ID of the view as specified by the extension. */
	public static final String ID = "inspectorguidget.eclipse.views.GUIBugsView";

	
	public GUIBugsView() {
		INSTANCE = this;
	}


	public static GUIBugsView getSingleton() {
		if(INSTANCE == null) {
			INSTANCE = new GUIBugsView();
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
				return DetectGUIBugsAction.getLabel((IMarker) element);
			}
		};
	}


	@Override
	protected ICheckStateListener getCheckStateListener() {
		return event -> {
//			String file = Activator.getDefault().getPreferenceStore().getString(PreferencePage.PATH_STORE);
//			if(!file.endsWith("/")) file +="/";
//			file+="GUIbugs.log";
//			String info = DetectGUIBugsAction.getInfo((IMarker) event.getElement());
//			FileHelper.appendFile(file, info + ";" + event.getChecked());
		};
	}
}