package inspectorguidget.eclipse.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class DetectGUIListenerHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
//		new ClearMarkersAction().run(null);
		new DetectGUIListenerAction().run(null);
		return null;
	}
}