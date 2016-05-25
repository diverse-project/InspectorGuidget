package inspectorguidget.eclipse.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;

import inspectorguidget.eclipse.Activator;
import inspectorguidget.eclipse.actions.DetectBlobListenerAction;
import inspectorguidget.eclipse.helper.FileHelper;
import inspectorguidget.eclipse.preferences.PreferencePage;

public class BlobView extends InspectorGuidgetView {
	private static BlobView	INSTANCE;
	
	private static final String VIEW_TITLE = "Blob Listeners";

	/** The ID of the view as specified by the extension. */
	public static final String ID = "inspectorguidget.eclipse.views.BlobView";

	
	public BlobView() {
		INSTANCE = this;
	}


	public static BlobView getSingleton() {
		if(INSTANCE == null) {
			INSTANCE = new BlobView();
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
				return DetectBlobListenerAction.getMethod((IMarker) element);
			}
		};
	}


	@Override
	protected ICheckStateListener getCheckStateListener() {
		return event -> {
			String file = Activator.getDefault().getPreferenceStore().getString(PreferencePage.PATH_STORE);
			if(!file.endsWith("/")) file +="/";
			file+="blobs.log";
			String info = DetectBlobListenerAction.getInfo((IMarker) event.getElement());
			FileHelper.appendFile(file, info + ";" + event.getChecked());
		};
	}
}