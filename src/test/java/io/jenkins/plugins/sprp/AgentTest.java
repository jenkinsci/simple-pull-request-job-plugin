package io.jenkins.plugins.sprp;

import hudson.Launcher;
import io.jenkins.plugins.sprp.models.Agent;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AgentTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private final Launcher launcher = (Launcher) jenkinsRule.createLocalLauncher();
    private PipelineSnippetGenerator pipelineSnippetGenerator = new PipelineSnippetGenerator(launcher);

    @Test
    public void nodeGenerationTest() {
        Agent agent = new Agent();
        agent.setLabel("my-label");
        agent.setCustomWorkspace("my-customWorkspace");

        String agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        String agentSnippetExpected =
                "agent {\n" +
                        "\tnode {\n" +
                        "\t\tlabel 'my-label'\n" +
                        "\t\tcustomWorkspace 'my-customWorkspace'\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);
    }

    @Test
    public void dockerGenerationTest() {
        Agent agent = new Agent();
        agent.setDockerImage("my-docker-image");
        agent.setArgs("-v temp:temp");

        String agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        String agentSnippetExpected =
                "agent {\n" +
                        "\tdocker {\n" +
                        "\t\timage 'my-docker-image'\n" +
                        "\t\targs '-v temp:temp'\n" +
                        "\t\talwaysPull false\n" +
                        "\t\treuseNode false\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);

        agent.setAlwaysPull(true);
        agent.setReuseNode(true);

        agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        agentSnippetExpected =
                "agent {\n" +
                        "\tdocker {\n" +
                        "\t\timage 'my-docker-image'\n" +
                        "\t\targs '-v temp:temp'\n" +
                        "\t\talwaysPull true\n" +
                        "\t\treuseNode true\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);

        agent.setAlwaysPull(false);
        agent.setReuseNode(false);

        agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        agentSnippetExpected =
                "agent {\n" +
                        "\tdocker {\n" +
                        "\t\timage 'my-docker-image'\n" +
                        "\t\targs '-v temp:temp'\n" +
                        "\t\talwaysPull false\n" +
                        "\t\treuseNode false\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);
    }


    @Test
    public void dockerfileGenerationTest() {
        Agent agent = new Agent();
        agent.setDockerfile("my-dockerfile");
        agent.setArgs("-v temp:temp");

        String agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        String agentSnippetExpected =
                "agent {\n" +
                        "\tdockerfile {\n" +
                        "\t\tfilename 'my-dockerfile'\n" +
                        "\t\tadditionalBuildArgs '-v temp:temp'\n" +
                        "\t\treuseNode false\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);

        agent.setLabel("my-label");
        agent.setCustomWorkspace("my-customWorkspace");
        agent.setDir("dir-path");
        agent.setReuseNode(true);

        agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        agentSnippetExpected =
                "agent {\n" +
                        "\tdockerfile {\n" +
                        "\t\tfilename 'my-dockerfile'\n" +
                        "\t\tdir 'dir-path'\n" +
                        "\t\tadditionalBuildArgs '-v temp:temp'\n" +
                        "\t\tlabel 'my-label'\n" +
                        "\t\tcustomWorkspace 'my-customWorkspace'\n" +
                        "\t\treuseNode true\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);

        agent.setReuseNode(false);

        agentSnippetActual =
                pipelineSnippetGenerator.autoAddTabs((ArrayList<String>) pipelineSnippetGenerator.getAgent(agent));

        agentSnippetExpected =
                "agent {\n" +
                        "\tdockerfile {\n" +
                        "\t\tfilename 'my-dockerfile'\n" +
                        "\t\tdir 'dir-path'\n" +
                        "\t\tadditionalBuildArgs '-v temp:temp'\n" +
                        "\t\tlabel 'my-label'\n" +
                        "\t\tcustomWorkspace 'my-customWorkspace'\n" +
                        "\t\treuseNode false\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);
    }
}
