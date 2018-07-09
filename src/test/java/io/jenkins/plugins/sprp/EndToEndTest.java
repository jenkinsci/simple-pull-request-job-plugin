package io.jenkins.plugins.sprp;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class EndToEndTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void verifyIsUnix() {
        boolean isUnixFormPipelineSnippetGenerator = new PipelineSnippetGenerator(jenkinsRule.createLocalLauncher()).isUnix();
        boolean isUnixFormJenkinsRule = jenkinsRule.createLocalLauncher().isUnix();
        assertEquals(isUnixFormPipelineSnippetGenerator, isUnixFormJenkinsRule);
    }

    @Test
    public void yamlToPipelineTest() throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        String yaml = "agent: any\n" +
                "\n" +
                "stages:\n" +
                "    - name: First\n" +
                "      steps:\n" +
                "        - sh './scripts/hello'\n" +
                "        - sleep 5\n" +
                "    - name: Build\n" +
                "      steps:\n" +
                "        - stepName: sh\n" +
                "          parameters:\n" +
                "            script: './scripts/build'\n" +
                "    - name: Tests\n" +
                "      steps:\n" +
                "        - stepName: sh\n" +
                "          defaultParameter: ./scripts/hello\n" +
                "\n" +
                "archiveArtifacts:\n" +
                "    - Jenkinsfile.yaml\n" +
                "    - scripts/hello.sh\n" +
                "\n";

        String pipelineScriptLinux = "pipeline {\n" +
                "\tagent any\n" +
                "\tstages {\n" +
                "\t\tstage('First') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh './scripts/hello.sh'\n" +
                "\t\t\t\tsleep 5\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\tstage('Build') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh './scripts/build.sh'\n" +
                "\t\t\t}\n" +
                "\t\t\tpost {\n" +
                "\t\t\t\tsuccess {\n" +
                "\t\t\t\t\tarchiveArtifacts artifacts: '**/target/*.jar'\n" +
                "\t\t\t\t\tarchiveArtifacts artifacts: 'Jenkinsfile.yaml'\n" +
                "\t\t\t\t\tarchiveArtifacts artifacts: 'scripts/hello.sh'\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\tstage('Tests') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh './scripts/hello.sh'\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        String pipelineScriptWindows = "pipeline {\n" +
                "\tagent any\n" +
                "\tstages {\n" +
                "\t\tstage('First') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh './scripts/hello.bat'\n" +
                "\t\t\t\tsleep 5\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\tstage('Build') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh './scripts/build.bat'\n" +
                "\t\t\t}\n" +
                "\t\t\tpost {\n" +
                "\t\t\t\tsuccess {\n" +
                "\t\t\t\t\tarchiveArtifacts artifacts: '**/target/*.jar'\n" +
                "\t\t\t\t\tarchiveArtifacts artifacts: 'Jenkinsfile.yaml'\n" +
                "\t\t\t\t\tarchiveArtifacts artifacts: 'scripts/hello.sh'\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\tstage('Tests') {\n" +
                "\t\t\tsteps {\n" +
                "\t\t\t\tsh './scripts/hello.bat'\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        StringInputStream yamlInputStream = new StringInputStream(yaml);
        if(jenkinsRule.createLocalLauncher().isUnix()){
            assertEquals(pipelineScriptLinux,
                    new YamlToPipeline().generatePipeline(yamlInputStream,
                            new GitConfig(), jenkinsRule.createTaskListener()));
        }
        else {
            assertEquals(pipelineScriptWindows,
                    new YamlToPipeline().generatePipeline(yamlInputStream,
                            new GitConfig(), jenkinsRule.createTaskListener()));
        }

    }
}
