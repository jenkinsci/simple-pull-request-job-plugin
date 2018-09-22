package io.jenkins.plugins.sprp;

import io.jenkins.plugins.sprp.exception.ConversionException;
import io.jenkins.plugins.sprp.git.GitConfig;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class YamlToPipelineTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void yamlToPipelineTest() throws ConversionException {
        String yaml = "agent: any\n" +
                "\n" +
                "stages:\n" +
                "  - name: stage1\n" +
                "    agent: any\n" +
                "    steps:\n" +
                "      - sh: \"scripts/hello.sh\"\n" +
                "      - sh:\n" +
                "          script: \"scripts/hello.sh\"" +
                "\n" +
                "configuration:\n" +
                "  archiveArtifacts:\n" +
                "      - Jenkinsfile.yaml\n" +
                "      - scripts/hello.sh\n" +
                "\n";

        String pipelineScriptLinuxExpected = "pipeline {\n" +
                "\tagent any\n" +
                "\tstages {\n" +
                "\t\tstage('stage1') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh 'scripts/hello.sh'\n" +
                "\t\t\t\tsh 'scripts/hello.sh'\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\tstage('Archive artifacts') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tarchiveArtifacts artifacts: 'Jenkinsfile.yaml'\n" +
                "\t\t\t\tarchiveArtifacts artifacts: 'scripts/hello.sh'\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        StringInputStream yamlInputStream = new StringInputStream(yaml);
        String pipelineScriptActual = new YamlToPipeline().generatePipeline(yamlInputStream,
                new GitConfig(), jenkinsRule.createTaskListener());

        assertEquals(pipelineScriptLinuxExpected, pipelineScriptActual);
    }
}
