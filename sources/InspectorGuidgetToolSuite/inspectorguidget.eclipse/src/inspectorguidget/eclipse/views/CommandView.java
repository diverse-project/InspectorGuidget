package inspectorguidget.eclipse.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;

import inspectorguidget.eclipse.Activator;
import inspectorguidget.eclipse.actions.DetectGUICommandAction;
import inspectorguidget.eclipse.helper.FileHelper;
import inspectorguidget.eclipse.preferences.PreferencePage;

public class CommandView extends InspectorGuidgetView {
	private static CommandView	INSTANCE;
	
	private static final String VIEW_TITLE = "GUI commands";

	/** The ID of the view as specified by the extension. */
	public static final String ID = "inspectorguidget.eclipse.views.CommandView";

	
	public CommandView() {
		INSTANCE = this;
	}


	public static CommandView getSingleton() {
		if(INSTANCE == null) {
			INSTANCE = new CommandView();
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
				return DetectGUICommandAction.getLabel((IMarker) element);
			}
		};
	}


	@Override
	protected ICheckStateListener getCheckStateListener() {
		return event -> {
			String file = Activator.getDefault().getPreferenceStore().getString(PreferencePage.PATH_STORE);
			String info = DetectGUICommandAction.getInfo((IMarker) event.getElement());
			FileHelper.appendFile(file, info + ";" + event.getChecked());
		};
	}
}