package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.PipelineGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@For(ArchiveArtifactStageGenerator.class)
public class ArchiveArtifactStageGeneratorTest {
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private ArchiveArtifactStageGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(ArchiveArtifactStageGenerator.class);
    }

    @Test
    public void echo() {
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
