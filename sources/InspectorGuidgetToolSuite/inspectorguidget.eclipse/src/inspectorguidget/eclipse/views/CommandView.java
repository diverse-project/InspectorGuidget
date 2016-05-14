package inspectorguidget.eclipse.views;

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
}