InspectorGuidget is a tool to detect GUI design smells such as blob listeners in java SWING/AWT GUI listeners by raising warnings in the Eclipse Java editor.


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