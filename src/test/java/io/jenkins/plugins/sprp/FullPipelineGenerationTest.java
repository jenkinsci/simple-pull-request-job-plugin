package io.jenkins.plugins.sprp;

import io.jenkins.plugins.sprp.exception.ConversionException;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class FullPipelineGenerationTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void test1() throws ConversionException {
        String yamlPipeline =
                "agent:\n" +
                "  label: my_label\n" +
                "\n" +
                "  tools:\n" +
                "    maven : maven_3.0.1\n" +
                "    jdk : jdk8\n" +
                "\n" +
                "configuration:\n" +
                "  pushPrOnSuccess: false\n" +
                "\n" +
                "  prApprovers:\n" +
                "    - username1\n" +
                "    - username2\n" +
                "    - username3\n" +
                "\n" +
                "  reports:\n" +
                "    - location_of_report_1\n" +
                "    - location_of_report_2\n" +
                "\n" +
                "environment:\n" +
                "  variables:\n" +
                "    variable_1: value_1\n" +
                "    variable_2: value_2\n" +
                "\n" +
                "  credentials:\n" +
                "    - credentialId : fileCredentialId\n" +
                "      variable : FILE\n" +
                "\n" +
                "    - credentialId : dummyGitRepo\n" +
                "      variable : LOGIN\n" +
                "\n" +
                "stages:\n" +
                "  - name: satge1\n" +
                "    agent: any\n" +
                "    steps:\n" +
                "      - sh:\n" +
                "          script: \"scripts/hello\"\n" +
                "      - sh: \"scripts/hello\"\n" +
                "      - sleep:\n" +
                "          time: 2\n" +
                "      - sleep: 2\n" +
                "\n" +
                "\n" +
                "    post:\n" +
                "      failure:\n" +
                "        - sh: \"scripts/hello\"\n" +
                "post:\n" +
                "  always:\n" +
                "    - sh: \"scripts/hello\"";

        YamlToPipeline yamlToPipeline = new YamlToPipeline();

        StringInputStream inputStream = new StringInputStream(yamlPipeline);

        String actualPipeline = yamlToPipeline.generatePipeline(inputStream,null, jenkinsRule.createTaskListener());

        String expectedPipeline =
                "pipeline {\n" +
                "\tagent {\n" +
                "\t\tnode {\n" +
                "\t\t\tlabel 'my_label'\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\ttools {\n" +
                "\t\tmaven 'maven_3.0.1'\n" +
                "\t\tjdk 'jdk8'\n" +
                "\t}\n" +
                "\tenvironment {\n" +
                "\t\tvariable_1 = 'value_1'\n" +
                "\t\tvariable_2 = 'value_2'\n" +
                "\t\tFILE = credentials('fileCredentialId')\n" +
                "\t\tLOGIN = credentials('dummyGitRepo')\n" +
                "\t}\n" +
                "\tstages {\n" +
                "\t\tstage('satge1') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh 'scripts/hello'\n" +
                "\t\t\t\tsh 'scripts/hello'\n" +
                "\t\t\t\tsleep 2\n" +
                "\t\t\t\tsleep 2\n" +
                "\t\t\t}\n" +
                "\t\t\tpost {\n" +
                "\t\t\t\tfailure {\n" +
                "\t\t\t\t\tsh 'scripts/hello'\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\tstage('Publish reports & artifacts') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tjunit 'location_of_report_1'\n" +
                "\t\t\t\tjunit 'location_of_report_2'\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\tpost {\n" +
                "\t\talways {\n" +
                "\t\t\tsh 'scripts/hello'\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        assertEquals(expectedPipeline, actualPipeline);
    }
}
