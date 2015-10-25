# InspectorGuidget
A tool to detect GUI design smells such as blob listeners in java SWING/AWT GUI listeners by raising warnings in the Eclipse Java editor.


To build the installer of InspectorGuidget:

  1. Import this project in Eclipse (Eclipse Modeling Framework is recommended).
  2. Generate the InspectorGuidgetXXX.jar as an eclispe plugin.
  3. Add the generated .jar in the folder "dropin" in our eclipse folder.
  
To run InspectorGuidget:
  1. Configure a project that will be analysed as an Eclipse project.
  2. Right click on the project in the navigator view.
  3. (Optional) Update the path in Eclipse>Preferences>InspectorGuidget to store the results. 
  4. Select InspectorGuidget and the target detection.
  5. The corresponding results for each detection will show up at the bottom view of eclipse.
  6. Left click on each result will open the corresponding piece of code.
  7. (Optional) To store the results (e.g., detected GUI listeners, blob listeners) check each one in that view, this will create a txt file in that path.

## An Empirical Study on GUI Listeners: Data

The sources 13 open-source Java projects:<br/>
argoUML, git clone https://github.com/cflewis/argouml, git checkout 0b6aeccaa2fd2e3e184f16bb1266edddd765be4d<br/>
cids-navigator, git clone https://github.com/cismet/cids-navigator, git checkout 3e856fa32c62c940151f812b975ae91de557d401<br/>
cids-server, git clone https://github.com/cismet/cids-server, git checkout 4cf8a4dcd05020af7282a5002b34cc1e81fe299e<br/>
cismet-gui-commons, git clone https://github.com/cismet/cismet-gui-commons/, git checkout 648b7d48bd6e5af417b90701bbb8b5791f58379c<br/>
DJ-Native-Swing, git clone https://github.com/Chrriis/DJ-Native-Swing, git checkout 3cffc438289472aaac5b79756c96e288f3ab477b<br/>
flyingsaucer, git clone https://github.com/flyingsaucerproject/flyingsaucer.git, git checkout d10728c2d5864c2a6d41ceb9f588a1ddbeacb7f2<br />
FreeCol, git clone https://github.com/Thue/FreeCol.git, git checkout 4454e5e6960487242cb2e5a8236eb6de9ab06c30<br/>
freeplane, git clone https://github.com/freeplane/freeplane.git, git checkout 1.4.x<br/>
ganttproject, git clone https://github.com/bardsoftware/ganttproject, git checkout c4b2e4a6cc3cd96ce720b013050b919157d9b7d8<br/>
pentaho-reporting, git clone https://github.com/pentaho/pentaho-reporting.git, git checkout cff612a0b6df36dc14af66afc35cfc429209d7be<br/>
RSyntaxTextArea, git clone https://github.com/bobbylight/RSyntaxTextArea.git, git checkout d84a498dd8c2e042ced9ccde4c96ea1d54770f68<br/>
sikuli, git clone https://github.com/sikuli/sikuli.git, git checkout 80c2f3fa03d95d657e7707ce2d1f576b3512a8b2<br/>
jabref, git clone https://github.com/JabRef/jabref.git, git checkout d9ba745225f56faf878b6538bda25969a996c968<br/>

The [logs](https://github.com/diverse-project/InspectorGuidget/blob/master/ICST16/results.tar.xz) resulting from our tool.<br/>
The [R script](https://github.com/diverse-project/InspectorGuidget/blob/master/ICST16/script.R) used to analyse the results.


## Examples of Blob Listeners GUI toolkits

### Swing

```java
public class MenuListener implements ActionListener, CaretListener {
 //...
 protected boolean selectedText;

@Override public void actionPerformed(ActionEvent e) {
  Object src = e.getSource();
  if(src instanceof JMenuItem || src instanceof JButton){
		 String cmd = e.getActionCommand();
		 if(cmd.equals("Copy")){
			 if(selectedText)
				 output.copy();
		 }else if(cmd.equals("Cut")){
			  output.cut();
		 }else if(cmd.equals("Paste")){
			 output.paste();
		 }
		 // etc.
		}
  }
  @Override public void caretUpdate(CaretEvent e){
   	selectedText = e.getDot() != e.getMark();
   	updateStateOfMenus(selectedText);	
 }
}
```

```java
public void actionPerformed(ActionEvent event) {
   if(event.getSource() == view.moveDown) {
      //...
   } else if(event.getSource() == view.moveLeft) {
      //...
   } else if(event.getSource() == view.moveRight) {
      //...
   } else if(event.getSource() == view.moveUp) {
      //...
   } else if(event.getSource() == view.zoomIn) {
      //...
   } else if(event.getSource() == view.zoomOut) {
      //...
   }
}
```

```java
public void actionPerformed(ActionEvent evt) {
   Object target = evt.getSource();
   if (target instanceof JButton) {
      //...
   } else if (target instanceof JTextField) {
      //...
   } else if (target instanceof JCheckBox) {
      //...
   } else if (target instanceof JComboBox) {
      //...
   }
}
```

### SWT

```java
public class SelectionEventListener extends AbstractListener implements Listener {
	private int selectMethod;
	
	@Override public void handleEvent(Event event) {
		if (event.type == SWT.MouseDown){
			handleMouse(event);
			return;
		}
		if (!checkCreate(event)) return;
		AbstractAction action = null;

		if (event.widget instanceof MenuItem){
			action = new SimpleSelectionAction(event.type);
			
		} else if (event.widget instanceof ToolItem){
			System.out.println("tool");
			
		} else if (event.widget instanceof Text){
			System.out.println("text");
			
		} else if (event.widget instanceof Button){
			action = new SimpleSelectionAction(event.type);
			action.setShell(((Button) event.widget).getShell());

		} else if (event.widget instanceof Tree){
			StringBuilder sb = new StringBuilder();
			Tree tr = (Tree) event.widget;
			System.out.println("tree");
			//do not handle events of workbench window 
			
			if ((!(tr.getShell() == PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell())) ||
					selectMethod == 3){
				System.out.println("the shell "+tr.getShell());
				TreeItem[] items = ((Tree) event.widget).getSelection();

				for (TreeItem item : items){
					//System.out.println("tree item "+ item.toString());
					sb.append(item.getText());
					TreeItem selected = item;

					while(selected.getParentItem() != null){
						TreeItem parentIt = selected.getParentItem();
						//System.out.println(parentIt);
						sb.insert(0, "/");
						sb.insert(0, parentIt.getText());
						selected = parentIt;
					}
				}
				System.out.println(sb.toString());
				action = new SelectionAction(event.type);
				action.process(event);
				interactionHistory.addInteraction(action);
			}
			return;
		}
		
		else if (event.widget instanceof Table){
			System.out.println("table");
			if (event.detail == SWT.CHECK)
				System.out.println("check");
			String type = event.type == SWT.DefaultSelection ? SelectionAction.DEFAULT_SELECT : SelectionAction.ITEM_SELECT;
			action = new SelectionAction(event.type);
			((SelectionAction) action).addPart(DeliasUtils.getActivePartTitle());
		}
        ...
```
[source](https://github.com/beccsi/delias/blob/a58df6ef45328d6b1495c424293e776a02e33e48/de.mobis.delias/src/org/teamweaver/delias/commons/SelectionEventListener.java)
[cache](examples/delias-a58df6ef45328d6b1495c424293e776a02e33e48.zip)

```java
    @Override
    public void handleEvent(Event event) {
        if (!(event.widget instanceof Button)){
            return;
        }
        Button b = (Button) event.widget;
        if (b.getText().equals(addButtonText)) {
            add();
        } else if (b.getText().equalsIgnoreCase(removeButtonText)) {
            remove();
        }
    }
```

[source](https://github.com/Unidata/awips2/blob/c9f28fd5943170b88cac2e3af3b0234ac444b705/cave/com.raytheon.uf.viz.collaboration.ui/src/com/raytheon/uf/viz/collaboration/ui/login/ServerListListener.java)
[cache](examples/awips2-upc_14.4.1.zip)

### GWT

```java
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == clearDatastoreButton) {
			clearDatastore();
		} else if (event.getSource() == populateDatastoreButton) {
			populateDatastore();
		} else if (event.getSource() == getPopulateDatastoreCountButton) {
			getPopulateDatastoreCount();
		} else if (event.getSource() == refreshEditModeOn) {
			refreshEditMode(true);
		} else if (event.getSource() == refreshEditModeOff) {
			refreshEditMode(false);
		} else if (event.getSource() == emailOpenedTree) {
			emailOpenedTree();
		} else if (event.getSource() == showOpenedTree) {
			showOpenedTree();
		}
	}
```

[source](https://github.com/zackriegman/ConceptMapper/blob/12ac53b27950e0b1c12dc803e57c355c675f4905/src/org/argmap/client/ModeAdmin.java)
[cache](examples/ConceptMapper-12ac53b27950e0b1c12dc803e57c355c675f4905.zip)

