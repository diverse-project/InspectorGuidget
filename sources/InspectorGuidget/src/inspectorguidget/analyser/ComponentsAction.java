package inspectorguidget.analyser;

import java.util.ArrayList;

import java.util.List;

/*
 * This class represents methods to get/set properties of widgets/components
 */
public class ComponentsAction {

	List<String> setActions;
	List<String> getActions;
	
	public ComponentsAction(){
		
		//instantiation of swing components
		getActions = new ArrayList<String>();
		setActions = new ArrayList<String>();
	
		
		//For all components
		setActions.add("setActionCommand"); 
		getActions.add("getActionCommand");
		setActions.add("setToolTipText");
		getActions.add("getToolTipText");
		
		//For all buttons
		setActions.add("setText");
		getActions.add("getText");
		setActions.add("setMnemonic");
		getActions.add("getMnemonic");
		
		
		//All buttons except of JCheckBox, JToggleButton and JRadioButton.
		setActions.add("setIcon");
		getActions.add("getIcon");
		
		
		//All JMenuItem subclasses, with the exception of JMenu.
		setActions.add("setKeyAccelerator");
		getActions.add("getKeyAccelerator");
		
		setActions.add("setAction");
		getActions.add("getAction");
		setActions.add("setPreferredSize");	
		getActions.add("getPreferredSize");	
		setActions.add("setAccelerator");
		getActions.add("getAccelerator");
		
		//instantiation of awt components
		setActions.add("setName");
		getActions.add("getName");
		
		//Menu Item
		setActions.add("setLabel");
		getActions.add("getLabel");
		setActions.add("setShortcut");
		getActions.add("getShortcut");
		setActions.add("paramString"); //also menus, toolbar, radiobutton, textarea, checkbox
		getActions.add("paramString"); //also menus, toolbar, radiobutton, textarea, checkbox -> find an example 
		setActions.add("setDocument");//JTextField
		getActions.add("getDocument");//JTextField
		
		//Which properties we should consider to do the refactoring
		//JTable (e.g., getFirstRow, getLastRow, etc.)
		//JCombobox
		//JRadioButton
		//JSpinner
		
		//TODO: actionMap
			//setLayout
		//setAttribute
	}
	
	//Check if caller method is a property of a component
	public boolean isSetProperty(String name){
		for(String method : setActions){
			if(method.equals(name)) return true;
		}
		return false;
	}
	
	public boolean isGetProperty(String name){
		for(String method : getActions){
			if(method.equals(name)) return true;
		}
		return false;
	}
}
