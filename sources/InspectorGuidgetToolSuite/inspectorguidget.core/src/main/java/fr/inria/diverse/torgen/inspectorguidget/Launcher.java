package fr.inria.diverse.torgen.inspectorguidget;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import fr.inria.diverse.torgen.inspectorguidget.processor.ListenerProcessor;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.compiler.SpoonCompiler;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.Filter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

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

public class Launcher implements SpoonAPI {
    private SpoonCompiler modelBuilder;
    private List<String> processorTypes = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        Launcher launcher = new Launcher();
        launcher.run(args);
    }

    public Launcher() {
        modelBuilder = createCompiler();
    }


    @Override
    public void run(String[] args) {
        new ExecArg().parse(args, this);
        buildModel();
        process();
    }

    public void setSourceClasspath(String ... args) {
        modelBuilder.setSourceClasspath(args);
    }

    @Override
    public void addInputResource(String file) {
        modelBuilder.addInputSource(new File(file));
    }

    @Override
    public void setSourceOutputDirectory(String path) {

    }

    @Override
    public void setSourceOutputDirectory(File outputDirectory) {

    }

    @Override
    public void setOutputFilter(Filter<CtType<?>> typeFilter) {

    }

    @Override
    public void setOutputFilter(String... qualifedNames) {

    }

    @Override
    public void setBinaryOutputDirectory(String path) {

    }

    @Override
    public void setBinaryOutputDirectory(File outputDirectory) {

    }

    @Override
    public void addProcessor(String name) {
    }

    @Override
    public <T extends CtElement> void addProcessor(Processor<T> processor) {

    }

    @Override
    public void buildModel() {
        modelBuilder.build();
    }

    @Override
    public void process() {
        ListenerProcessor proc = new ListenerProcessor();
        modelBuilder.process(Arrays.asList(proc));
    }

    @Override
    public void prettyprint() {

    }

    @Override
    public void run() {

    }

    @Override
    public Factory getFactory() {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }

    @Override
    public Factory createFactory() {
        return new FactoryImpl(new DefaultCoreFactory(), createEnvironment());
    }

    @Override
    public Environment createEnvironment() {
        return new StandardEnvironment();
    }

    @Override
    public SpoonCompiler createCompiler() {
        return new JDTBasedSpoonCompiler(createFactory());
    }
}
