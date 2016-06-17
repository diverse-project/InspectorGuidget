package fr.inria.diverse.torgen.inspectorguidget;

import fr.inria.diverse.torgen.inspectorguidget.analyser.InspectorGuidetAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.FXMLAnnotationProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;
import spoon.processing.Processor;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *     <build>
 <plugins>
 <plugin>
 <groupId>org.apache.maven.plugins</groupId>
 <artifactId>maven-dependency-plugin</artifactId>
 <configuration>
 <includeScope>compile</includeScope>
 <excludeScope>test</excludeScope>
 <outputDirectory>
 target/deps/
 </outputDirectory>
 </configuration>
 </plugin>
 </plugins>
 </build>
 */

public class Launcher extends InspectorGuidetAnalyser {
    public static void main(String[] args) throws IOException {
        Launcher launcher = new Launcher(Arrays.asList(new ClassListenerProcessor(), new LambdaListenerProcessor(),
			new FXMLAnnotationProcessor(), new WidgetProcessor()));
        launcher.run(args);
    }

    public Launcher(final List<Processor<?>> procs) {
		super(procs);
    }

	@Override
	public CtModel getModel() {
		return null;
	}
}
