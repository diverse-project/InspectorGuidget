package inspectorguidgetplugin.preferences;

import inspectorguidgetplugin.Activator;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage{

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Files to store results of the InspectorGuidget views");
	}
	
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor("pathBlobListeners", 
				"Blob listeners: ", getFieldEditorParent()));
		addField(new StringFieldEditor("pathBlobListeners2", 
				"Blob listeners2: ", getFieldEditorParent()));
		addField(new StringFieldEditor("pathCommand", 
				"Commands: ", getFieldEditorParent()));
		addField(new StringFieldEditor("pathCondListeners", 
				"Cond listeners: ", getFieldEditorParent()));
		addField(new StringFieldEditor("pathListeners", 
				"Listeners: ", getFieldEditorParent()));
	}
}
