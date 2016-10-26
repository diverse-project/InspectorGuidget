package inspectorguidget.eclipse.resolutions;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class BlobRefactoringGenerator implements IMarkerResolutionGenerator {
	@Override
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		return new IMarkerResolution[]{new BlobMarkerResolution()};
	}
}
