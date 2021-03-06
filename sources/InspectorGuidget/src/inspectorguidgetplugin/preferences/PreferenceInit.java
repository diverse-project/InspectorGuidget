package inspectorguidgetplugin.preferences;

import inspectorguidgetplugin.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInit extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault("pathBlobListeners", "CheckedBlobListeners.txt");
		store.setDefault("pathBlobListeners2", "CheckedBlobListeners2.txt");
		store.setDefault("pathCommand", "CheckedCommand.txt");
		store.setDefault("pathCondListeners", "CheckedCondListeners.txt");
		store.setDefault("pathListeners", "CheckedListeners.txt");
	}

}
