package io.jenkins.plugins.sprp.generators;

import io.jenkins.plugins.sprp.PipelineGenerator;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@For(ArchiveArtifactStageGenerator.class)
public class ArchiveArtifactStageGeneratorTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private ArchiveArtifactStageGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(ArchiveArtifactStageGenerator.class);
    }

    @Test
    public void echo() throws IllegalAccessException, InvocationTargetException, ConfiguratorException, InstantiationException, NoSuchMethodException, NotSupportedException {
        ArrayList<String> archiveArtifacts = new ArrayList<>();

        archiveArtifacts.add("path-to-archive-1");
        archiveArtifacts.add("path-to-archive-2");
        archiveArtifacts.add("path-to-archive-3");

        String pipelineStepActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(archiveArtifacts));
        String pipelineStepExpected =
                "stage('Archive artifacts') {\n" +
                "\tsteps {\n" +
                "\t\tarchiveArtifacts artifacts: 'path-to-archive-1'\n" +
                "\t\tarchiveArtifacts artifacts: 'path-to-archive-2'\n" +
                "\t\tarchiveArtifacts artifacts: 'path-to-archive-3'\n" +
                "\t}\n" +
                "}\n";

        assertEquals(pipelineStepExpected, pipelineStepActual);
    }
}
