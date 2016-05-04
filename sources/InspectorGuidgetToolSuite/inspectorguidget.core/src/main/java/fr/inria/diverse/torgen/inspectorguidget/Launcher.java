package fr.inria.diverse.torgen.inspectorguidget;

import fr.inria.diverse.torgen.inspectorguidget.analyser.InspectorGuidetAnalyser;
import fr.inria.diverse.torgen.inspectorguidget.processor.ClassListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.FXMLAnnotationProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.LambdaListenerProcessor;
import fr.inria.diverse.torgen.inspectorguidget.processor.WidgetProcessor;

import java.io.IOException;
import java.util.Arrays;

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
        Launcher launcher = new Launcher();
        launcher.run(args);
    }

    public Launcher() {
		super(Arrays.asList(new ClassListenerProcessor(), new LambdaListenerProcessor(),
				new FXMLAnnotationProcessor(), new WidgetProcessor()));
    }
}
