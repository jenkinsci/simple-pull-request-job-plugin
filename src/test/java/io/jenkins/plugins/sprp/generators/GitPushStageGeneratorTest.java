package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.GitConfig;
import io.jenkins.plugins.sprp.PipelineGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@For(GitPushStageGenerator.class)
public class GitPushStageGeneratorTest {
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private GitPushStageGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(GitPushStageGenerator.class);
    }

    @Test
    public void gitPushStage() {
        GitConfig gitConfig = new GitConfig();
        gitConfig.setGitBranch("branchName");
        gitConfig.setCredentialsId("gitCredentialId");
        gitConfig.setGitUrl("gitRepoUrl");

        String pipelineStepActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(gitConfig));
        String pipelineStepExpected =
                "stage('Git Push') {\n" +
                "\tsteps {\n" +
                "\t\tgitPush credentialId: \"gitCredentialId\",url: \"gitRepoUrl\",branch: \"branchName\"\n" +
                "\t}\n" +
                "}\n";

        assertEquals(pipelineStepExpected, pipelineStepActual);
    }
}
