package io.jenkins.plugins.sprp.generators;

import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Credential;
import io.jenkins.plugins.sprp.models.Environment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@For(EnvironmentGenerator.class)
public class EnvironmentGeneratorTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private EnvironmentGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(EnvironmentGenerator.class);
    }

    @Test
    public void env() {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("JAVA_HOME", "/pathToJavaHome/dir1/dir2");
        variables.put("MAVEN_HOME", "/pathToMavenHome/dir1/dir2");

        ArrayList<Credential> credentials = new ArrayList<>();

        Credential credential1 = new Credential();

        credential1.setCredentialId("fileCredentialId");
        credential1.setVariable("FILE");

        credentials.add(credential1);

        Credential credential2 = new Credential();

        credential2.setCredentialId("usernameAndPasswordCredentialId");
        credential2.setVariable("LOGIN");

        credentials.add(credential2);

        Environment environment = new Environment();
        environment.setVariables(variables);
        environment.setCredentials(credentials);

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(environment));

        String agentSnippetExpected =
                "environment {\n" +
                        "\tMAVEN_HOME = '/pathToMavenHome/dir1/dir2'\n" +
                        "\tJAVA_HOME = '/pathToJavaHome/dir1/dir2'\n" +
                        "\tFILE = credentials('fileCredentialId')\n" +
                        "\tLOGIN = credentials('usernameAndPasswordCredentialId')\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);
    }
}
