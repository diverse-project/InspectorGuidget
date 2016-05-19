package inspectorguidget.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import inspectorguidget.eclipse.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String PATH_STORE = "pathStoreInspectorGuidget";


	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Files where results of InspectorGuidget analyses are stored");
	}

	@Override
	public void init(final IWorkbench workbench) {
		// Nothing to do.
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(PATH_STORE, "Results stored: ", getFieldEditorParent()));
	}
}
