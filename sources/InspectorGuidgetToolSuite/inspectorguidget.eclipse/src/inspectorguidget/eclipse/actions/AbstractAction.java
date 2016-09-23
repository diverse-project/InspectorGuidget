package inspectorguidget.eclipse.actions;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.launching.JREContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import spoon.SpoonAPI;
import spoon.reflect.factory.Factory;

public abstract class AbstractAction<T extends SpoonAPI> implements IObjectActionDelegate {
	protected long spoonloading;
	protected long startTime;
	protected long analysisTime;
	protected T analyser;


	@Override
	public void run(IAction action) {
		final IProject project = getCurrentProject();

		Job job = new Job("InspectorGuidget.eclipse") {
			@Override
			public IStatus run(IProgressMonitor monitor) {
				// set total number of work units
				monitor.beginTask("Spoon analysis", 2);
				// Measuring the time for InspectorWidget
				// final long startTime = System.currentTimeMillis();
				initAction(monitor, project);
				Job job2 = new UIJob("Add markers") {// To switch to the UI thread
					@Override
					public IStatus runInUIThread(IProgressMonitor monit) {
						addMarkers(project);
						// Measuring the time of InspectorWidget
						// long endTime = System.currentTimeMillis();
						// System.out.println("---------------------------");
						// System.out.println("spoonLoading time " +
						// (spoonloading - startTime)); //The time just for
						// spoonloading
						// System.out.println("blob detection time " + (endTime
						// - spoonloading)); //The time just for blob detection
						// System.out.println("Total of time " + (endTime -
						// startTime));
						return Status.OK_STATUS;
					}
				};
				job2.schedule();

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	
	protected void loadProjectDeps(final Set<String> classpath, final Set<File> libs, final Set<String> projects,
									final IProject project, final boolean mainProject) {
		try {
			if(project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject jProject = JavaCore.create(project);

				for(IClasspathEntry entry : jProject.getRawClasspath()) {
					switch(entry.getEntryKind()) {
						case IClasspathEntry.CPE_SOURCE:
							IPath rel = entry.getPath().makeRelativeTo(project.getFullPath());

							if(rel.isEmpty()) {
								classpath.add(project.getLocation().toString());
							}else {
								classpath.add(project.getFile(rel).getLocation().toString());								
							}
							break;
						case IClasspathEntry.CPE_LIBRARY:
							File file = entry.getPath().toFile();
							
							if(file.exists()) {
								libs.add(file);	
							}else {
								file = new File(project.getFile(entry.getPath().makeRelativeTo(project.getFullPath())).getLocation().toString());
								
								if(file.exists()) {
									libs.add(file);
								} else {
									System.err.println("CPE_LIBRARY: cannot find file to add as lib: " + entry.getPath());
								}
							}
							 
							break;
						case IClasspathEntry.CPE_PROJECT:
							IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().toOSString());
							if(proj != null && !projects.contains(proj.getName())) {
								projects.add(proj.getName());
								loadProjectDeps(classpath, libs, projects, proj, false);
							}
							break;
						case IClasspathEntry.CPE_CONTAINER:
							try{
								final IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), jProject);
								
								if(container != null && !(container instanceof JREContainer)) {
									libs.addAll(Arrays.stream(container.getClasspathEntries()).map(en -> en.getPath().toFile()).collect(Collectors.toList()));
								}
							}catch(JavaModelException e) { e.printStackTrace(); }
							break;
						default:
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


	protected void initAction(final IProgressMonitor monitor, final IProject project) {
		monitor.subTask("Collect source files");
		startTime = System.currentTimeMillis();

		Set<String> classpath = new HashSet<>();
		Set<File> libs = new HashSet<>();
		Set<String> projects = new HashSet<>();

		projects.add(project.getName());
		loadProjectDeps(classpath, libs, projects, project, true);

		monitor.worked(1);
		monitor.subTask("Spoon build");
		spoonBuild(classpath, libs);
		monitor.subTask("InspectorGuidget analysis");
		monitor.worked(2);
		analyser.process();
		analysisTime = System.currentTimeMillis();
		monitor.worked(3);
		System.out.println("Code model built in " + (spoonloading - startTime));
		System.out.println("Analysis done in " + (analysisTime - spoonloading));
	}


	/**
	 * Build the Spoon AST for the classes in @classpath
	 * @param libs
	 * @return Spoon model
	 */
	protected Factory spoonBuild(Set<String> classpath, Set<File> libs) {
		ClassLoader libLoader = new URLClassLoader(getDependencies(libs), Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(libLoader);

		analyser = createAnalyser();
		System.out.println(classpath);
		System.out.println(libs);
		classpath.forEach(file -> analyser.addInputResource(file));

		try {
			analyser.buildModel();
		}catch(Exception e) {
			e.printStackTrace();
		}
//		env.setInputClassLoader(ClassLoader.getSystemClassLoader());
		spoonloading = System.currentTimeMillis();// Measuring the time of Spoon to load the classes
		return analyser.getFactory();
	}
	
	
	protected abstract T createAnalyser();
	

	private static URL[] getDependencies(final Set<File> libs) {
		return libs.stream().map(file -> {
			try { return file.toURI().toURL(); } 
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}).filter(dep -> dep!=null).toArray(URL[]::new);
	}


	abstract protected void addMarkers(IProject project);


	/** Get the selected project. Can return null */
	public static IProject getCurrentProject() {
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();

		if(selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if(element instanceof IProject) return (IProject) element;
			if(element instanceof IJavaProject) return ((IJavaProject)element).getProject();
			if(element instanceof IResource) return ((IResource)element).getProject();
		}

		return null;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
