package inspectorguidget.eclipse.views;

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
}