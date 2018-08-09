package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.ConversionException;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Stage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@For(StepGenerator.class)
public class StageGeneratorTest {
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private StageGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(StageGenerator.class);
    }

    @Test
    public void stage() throws ConversionException {
        String stageYaml =
                "name: satge1\n" +
                "agent: any\n" +
                "steps:\n" +
                "  - sh: \"scripts/hello\"\n" +
                "  - sh: \"scripts/hello\"\n" +
                "  - sh:\n" +
                "      script: \"scripts/hello\"\n" +
                "post:\n" +
                "  failure:\n" +
                "    - sh: \"scripts/hello\"";

        Yaml yaml = new Yaml();
        Stage stage = yaml.loadAs(stageYaml, Stage.class);

        String pipelineStageActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(stage));
        String pipelineStageExpected =
                "stage('satge1') {\n" +
                "\tsteps {\n" +
                "\t\tsh 'scripts/hello'\n" +
                "\t\tsh 'scripts/hello'\n" +
                "\t\tsh 'scripts/hello'\n" +
                "\t}\n" +
                "\tpost {\n" +
                "\t\tfailure {\n" +
                "\t\t\tsh 'scripts/hello'\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        assertEquals(pipelineStageExpected, pipelineStageActual);
    }
}
