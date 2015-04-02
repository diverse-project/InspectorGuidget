package inspectorguidgetplugin.popup.actions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.progress.UIJob;

import inspectorguidget.analyser.cfg.CfgBuilder;
import inspectorguidget.analyser.processor.wrapper.ListenersWrapper;
import spoon.compiler.SpoonCompiler;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public abstract class AbstractAction implements IObjectActionDelegate{
	private Shell shell;
	public Factory factory;
	long spoonloading;
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		final IProject project = getCurrentProject();
		final ListenersWrapper listeners = new ListenersWrapper();
		
		Job job = new Job("InspectorGuidget") {
			@Override
			public IStatus run(IProgressMonitor monitor) {
			  // set total number of work units
			  monitor.beginTask("Spoon analyze", 2);
			  //Measuring the time for InspectorWidget
			  //final long startTime = System.currentTimeMillis(); 
			  initAction(monitor,project);
			  Job job2 = new UIJob("Add markers") {//To switch to the UI thread
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						addMarkers(project);
						//Measuring the time of InspectorWidget
						//long endTime = System.currentTimeMillis();
						//System.out.println("---------------------------");
						//System.out.println("spoonLoading time " + (spoonloading - startTime)); //The time just for spoonloading
						//System.out.println("blob detection time " + (endTime - spoonloading)); //The time just for blob detection
						//System.out.println("Total of time " + (endTime - startTime));
						return Status.OK_STATUS;
					}
				};
				job2.schedule();
			  
			  return Status.OK_STATUS;
			  }
			};
		job.schedule(); 
	}
	
	protected void initAction(IProgressMonitor monitor, IProject project){
		
		monitor.subTask("Collect source files");
		
		List<File> classpath = new ArrayList<File>();
		List<File> libs = new ArrayList<File>();
		try {
			if(project.isOpen() && project.hasNature(JavaCore.NATURE_ID)){
				IJavaProject jProject = JavaCore.create(project);		
				
				for (IClasspathEntry entry : jProject.getRawClasspath()) {
					if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE){
						//String path = project.getFile(entry.getPath().lastSegment()).getLocation().toString();
						IPath rel = entry.getPath().makeRelativeTo(project.getFullPath());
						String path = project.getFile(rel).getLocation().toString();
						File file = new File(path);	
						classpath.add(file);
					}
					if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY){//Refactor
						IPath rel = entry.getPath().makeRelativeTo(project.getFullPath());
						String path = project.getFile(rel).getLocation().toString();
						File file = new File(path);	
						libs.add(file);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		monitor.worked(1);
		monitor.subTask("Spoon build");
		spoonProcess(spoonBuild(classpath, libs),buildProcessors());//listener are processed from here
		monitor.worked(2);
	}
	
	protected abstract List<AbstractProcessor> buildProcessors();

	/**
	 * Build the Spoon AST for the classes in @classpath
	 * @param libs 
	 * @return Spoon model
	 */
	protected Factory spoonBuild(List<File> classpath, List<File> libs){
	
		ClassLoader libLoader = new URLClassLoader(getDependencies(libs), Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(libLoader);
	
		
		
		StandardEnvironment env = new StandardEnvironment();
		DefaultCoreFactory f = new DefaultCoreFactory();
        factory = new FactoryImpl(f, env);
        SpoonCompiler comp = new JDTBasedSpoonCompiler(factory);
        CfgBuilder.factory = factory;
		
//		SpoonCompiler comp = new Launcher().createCompiler();
		
		for (File file : classpath) {
			try {
				comp.addInputSource(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		boolean success = false;
		try {
			success = comp.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		env.setInputClassLoader(ClassLoader.getSystemClassLoader());
		spoonloading = System.currentTimeMillis();//Measuring the time of Spoon to load the classes
		return comp.getFactory();
	}
	
	//private static URL[] getDependencies(List<String> folders){
	private static URL[] getDependencies(List<File> libs){
//		List<URL[]> urls = new ArrayList<URL[]>();
//		for(String folder : folders){
//			urls.add(getDependencies(folder));
//		}
		
		List<URL[]> urls = new ArrayList<URL[]>();
		//for(String folder : folders){
		for(File file : libs){
			urls.add(getDependencies(file));
		}
		
		int size = 0;
		for(URL[] url : urls){
			size += url.length;
		}
		
		URL[] res = new URL[size];
		size = 0;
		for(int i = 0; i < urls.size(); i++){
			URL[] url = urls.get(i);
			for(int j = 0; j < url.length; j++){
				res[size+j] = url[j];
			}
			size += url.length;
		}
		
		for(URL url : res){
			System.out.println(url);
		}
		
		return res;
	}
	
	
	//private static URL[] getDependencies(String folder){
		//TODO: check for errors :)
	private static URL[] getDependencies(File file){
		//try {
//			URL path;
//			File folderDir;
//			
//            path = new URL("file://"+ folder);
//            folderDir = new File(path.toURI());
//			
//			File[] libs = folderDir.listFiles();
			
			ArrayList<URL> urls = new ArrayList<URL>();
			//for(File file : libs){
				if(file.isDirectory()){
					//URL[] deeperFolders = getDependencies(file.getAbsolutePath());
					URL[] deeperFolders = getDependencies(file);
					for(URL r : deeperFolders){
						urls.add(r);
					}
				}
				else{
					try {
                        //					if(file.getName().endsWith(".jar")){
                        urls.add(file.toURI().toURL());
                        //					}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			//}
            return urls.toArray(new URL[urls.size()]);
            
//		} catch (MalformedURLException | URISyntaxException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//            
//		}
		//return null;
	}
	
	

	/**
	 * Find listeners in @factory and store them in @listeners
	 */
	protected void spoonProcess(Factory factory, List<AbstractProcessor> processors){
	
		ProcessingManager processorManager = new QueueProcessingManager(factory);
		for (AbstractProcessor proc: processors){
			processorManager.addProcessor(proc);
		}
		processorManager.process();
	}
	abstract protected void addMarkers(IProject project);
	
	/**
	 * Get the selected poject.
	 * Can return null
	 */
	protected static IProject getCurrentProject(){    
        ISelectionService selectionService =     
            Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    

        ISelection selection = selectionService.getSelection();    

        IProject project = null;    
        if(selection instanceof IStructuredSelection) {    
            Object element = ((IStructuredSelection)selection).getFirstElement();    
            return (IProject) element;

        }     
        return null;    
    }
	
	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
