package inspectorguidget.eclipse.views;

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
}