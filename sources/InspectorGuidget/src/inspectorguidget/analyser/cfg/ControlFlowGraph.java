package inspectorguidget.analyser.cfg;

import inspectorguidget.analyser.processor.SimpleListenerProcessor;
import inspectorguidget.analyser.processor.wrapper.ListenersWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import spoon.compiler.SpoonCompiler;
import spoon.processing.AbstractProcessor;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

/**
 * Main class that analyze the source code, compute the control flow graph of
 * the listeners and write them in .dot files format.
 */
public class ControlFlowGraph {

	/**
	 * All nodes of the control flow graph
	 */
	List<BasicBlock>	nodes	= new ArrayList<>();

	/**
	 * Internal representation
	 */
	SubGraph			graph;

	/**
	 * Source of the control flow graph
	 */
	CtExecutable<?>		method;

	/**
	 * Create a control flow graph from this method
	 */
	public ControlFlowGraph(CtExecutable<?> method) {
		this.method = method;
		nodes = new ArrayList<>();
		graph = CfgBuilder.build(method, this);
		clean();
	}

	/**
	 * Remove unnecessary Connector nodes
	 */
	private void clean() {
		ArrayList<BasicBlock> toBeRemoved = new ArrayList<>();
		for (BasicBlock node : this.nodes) {
			if (node instanceof Connector && node.getChildren().size() == 1) {
				BasicBlock child = node.getChildren().get(0);
				if (node.getCondition(child) == null) {
					toBeRemoved.add(node);
					for (BasicBlock parent : node.getParents()) {
						parent.getChildren().remove(node);
						parent.addChild(child, parent.getCondition(node));
						parent.conditions.remove(node);
					}
				}
			} else if (node instanceof Connector && node.getParents().size() == 0 && node.getChildren().size() == 0) {
				toBeRemoved.add(node);
			}
		}
		this.nodes.removeAll(toBeRemoved);
	}

	public void removeNode(BasicBlock node) {
		nodes.remove(node);
	}

	public void addNode(BasicBlock node) {
		nodes.add(node);
	}

	public List<BasicBlock> getAllNode() {
		return nodes;
	}

	/**
	 * Find all possibles execution path
	 */
	public List<List<BasicBlock>> getExecutionPaths() {
		return ExecutionPath.getPaths(graph);
	}

	/**
	 * Write the cfg in .dot format in the file 'outFolder'/'filename'
	 */
	public void writeDotGraph(String outFolder, String fileName) {
		StringBuffer fileContent = new StringBuffer();
		fileContent.append("digraph OutputGraph {");
		for (BasicBlock node : this.nodes) {
			fileContent.append(node);
		}
		fileContent.append("}");

		writeFile(outFolder, fileName + ".dot", fileContent.toString());
		System.out.println("CFG saved in " + outFolder + "/" + fileName);
	}

	/**
	 * Get the source of this control flow graph
	 */
	public CtExecutable<?> getExecutable() {
		return method;
	}

	/**
	 * Return the top block
	 */
	public BasicBlock getEntryBlock() {
		return graph.getEntry();
	}

	/**
	 * Return the top block
	 */
	public BasicBlock getExitBlock() {
		return graph.getExit();
	}

	public static void main(String[] args) {

		try {
			// System.out.println("Loading...");

			// final String outFolder = "dot/";

			// Locate source files
			final String sourceFolder = "src/test/";

			// Setup the factory
			StandardEnvironment env = new StandardEnvironment();
			DefaultCoreFactory f = new DefaultCoreFactory();
			final Factory factory = new FactoryImpl(f, env);
			CfgBuilder.factory = factory;

			// Build the model
			SpoonCompiler compiler = new JDTBasedSpoonCompiler(factory);
			compiler.addInputSource(new File(sourceFolder));
			compiler.build();

			// System.out.println("Loading done.");
			//
			// System.out.println("Processing...\n");

			// List<CtSimpleType<?>> allTypes = factory.Class().getAll();
			// final List<CtType> allClasses = new ArrayList();
			// for(CtSimpleType type : allTypes){
			// if(type instanceof CtType){
			// allClasses.add((CtType) type);
			// System.out.println(type);
			// }
			// }

			final ArrayList<CtType<?>> allClasses = new ArrayList<>();
			final ArrayList<CtClass<?>> allClasses2 = new ArrayList<>();

			env.setInputClassLoader(ClassLoader.getSystemClassLoader());
			ProcessingManager processorManager = new QueueProcessingManager(factory);
			processorManager.addProcessor(new AbstractProcessor<CtClass<?>>() {

				@Override
				public void process(CtClass<?> clazz) {
					allClasses.add(clazz);
					allClasses2.add(clazz);
				}
			});
			processorManager.addProcessor(new AbstractProcessor<CtMethod<?>>() {

				@Override
				public void process(CtMethod<?> method) {
					// // SubGraph content = CfgBuilder.process(method);
					// ControlFlowGraph cfg = new ControlFlowGraph(method);
					// StringBuffer fileContent = new StringBuffer();
					// fileContent.append("digraph OutputGraph {");
					// for(BasicBlock node : cfg.getAllNode()){
					// fileContent.append(node.toString());
					// }
					// fileContent.append("}");
					//
					// writeFile(outFolder,method.getSimpleName()+".dot",fileContent.toString());
					//
					// List exec = cfg.getExecutionPaths();
					// System.out.println("Execution paths = " + exec.size());
					//
					// CallGraph cg = new CallGraph(method,allClasses);

					// DefUse defuse = new DefUse(cfg);
					// System.out.println(defuse);
					// for(BasicBlock bloc : cfg.getAllNode()){
					// System.out.println(defuse.getDeepDef(bloc));
					// }

					// MyPrinter printer = new MyPrinter(getEnvironment(),cfg);
					// printer.scan(method);
					// System.out.println(method.getSimpleName()+" "+printer.getStartLine(method)+":"+printer.getEndLine(method));
					// System.out.println(printer.toString());

				}
			});
			// processorManager.addProcessor(new
			// AbstractProcessor<CtAssignment>() {
			//
			// @Override
			// public void process(CtAssignment arg0) {
			// System.out.println(arg0);
			//
			// }
			//
			// });
			// processorManager.addProcessor(new ComplexityProcessor());
			ListenersWrapper wrapper = new ListenersWrapper();
			processorManager.addProcessor(new SimpleListenerProcessor(wrapper));
			// Configuration config = new Configuration(new
			// File("src/test/test.config"));
			// VariablesProcessor processor = new VariablesProcessor(new
			// ListenerProcessor(config));
			// processorManager.addProcessor(processor);
			processorManager.process();
			for (CtMethod<?> listener : wrapper.getListeners()) {
				System.out.println(listener.getSimpleName());
			}
			// Analyzer engine = new Analyzer(allClasses2);
			// writeFile("callgraphDOT","variableType.dot",engine.toString());

			System.out.println("\nProcessing done.");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Write 'content' in 'dir'/'file'
	 */
	public static void writeFile(String dir, String file, String content) {
		File newDir = new File(dir);
		newDir.mkdirs();

		try (FileWriter fw = new FileWriter(dir + "/" + file, false); BufferedWriter output = new BufferedWriter(fw);) {
			output.write(content);
			output.flush();
			output.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
