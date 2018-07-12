package io.jenkins.plugins.sprp;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class IsUnixTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void verifyIsUnix() {
        boolean isUnixFormPipelineSnippetGenerator = new PipelineSnippetGenerator(jenkinsRule.createLocalLauncher()).isUnix();
        boolean isUnixFormJenkinsRule = jenkinsRule.createLocalLauncher().isUnix();
        assertEquals(isUnixFormPipelineSnippetGenerator, isUnixFormJenkinsRule);
    }
}
