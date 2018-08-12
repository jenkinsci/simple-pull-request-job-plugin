package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.ConversionException;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Post;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@For(PostGenerator.class)
public class PostGeneratorTest {
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private PostGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(PostGenerator.class);
    }

    @Test
    public void echo() throws ConversionException {
        String postYaml =
                "always:\n" +
                "  - sh: \"always.sh\"\n" +
                "changed:\n" +
                "  - sh: \"changed.sh\"\n" +
                "fixed:\n" +
                "  - sh: \"fixed.sh\"\n" +
                "regression:\n" +
                "  - sh: \"regression.sh\"\n" +
                "aborted:\n" +
                "  - sh: \"aborted.sh\"\n" +
                "failure:\n" +
                "  - sh: \"failure.sh\"\n" +
                "success:\n" +
                "  - sh: \"success.sh\"\n" +
                "unstable:\n" +
                "  - sh: \"unstable.sh\"\n" +
                "cleanup:\n" +
                "  - sh: \"cleanup.sh\"\n";

        Yaml yaml = new Yaml();
        Post postSection = yaml.loadAs(postYaml, Post.class);

        String pipelineStepActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(postSection));
        String pipelineStepExpected =
                "post {\n" +
                "\talways {\n" +
                "\t\tsh 'always.sh'\n" +
                "\t}\n" +
                "\tchanged {\n" +
                "\t\tsh 'changed.sh'\n" +
                "\t}\n" +
                "\tfixed {\n" +
                "\t\tsh 'fixed.sh'\n" +
                "\t}\n" +
                "\tregression {\n" +
                "\t\tsh 'regression.sh'\n" +
                "\t}\n" +
                "\taborted {\n" +
                "\t\tsh 'aborted.sh'\n" +
                "\t}\n" +
                "\tfailure {\n" +
                "\t\tsh 'failure.sh'\n" +
                "\t}\n" +
                "\tsuccess {\n" +
                "\t\tsh 'success.sh'\n" +
                "\t}\n" +
                "\tunstable {\n" +
                "\t\tsh 'unstable.sh'\n" +
                "\t}\n" +
                "\tcleanup {\n" +
                "\t\tsh 'cleanup.sh'\n" +
                "\t}\n" +
                "}\n";

        assertEquals(pipelineStepExpected, pipelineStepActual);
    }
}
