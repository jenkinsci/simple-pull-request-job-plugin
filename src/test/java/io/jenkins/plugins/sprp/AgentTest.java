package io.jenkins.plugins.sprp;

import io.jenkins.plugins.sprp.impl.AgentGenerator;
import io.jenkins.plugins.sprp.models.Agent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AgentTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private AgentGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(AgentGenerator.class);
    }

    @Test
    public void nodeGenerationTest() {
        Agent agent = new Agent();
        agent.setLabel("my-label");
        agent.setCustomWorkspace("my-customWorkspace");

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

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
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

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
    }

    @Test
    public void dockerGenerationTest1(){
        Agent agent = new Agent();
        agent.setDockerImage("my-docker-image");
        agent.setArgs("-v temp:temp");
        agent.setAlwaysPull(true);
        agent.setReuseNode(true);

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

        String agentSnippetExpected =
                "agent {\n" +
                        "\tdocker {\n" +
                        "\t\timage 'my-docker-image'\n" +
                        "\t\targs '-v temp:temp'\n" +
                        "\t\talwaysPull true\n" +
                        "\t\treuseNode true\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);
    }

    @Test
    public void dockerGenerationTest2(){
        Agent agent = new Agent();
        agent.setDockerImage("my-docker-image");
        agent.setArgs("-v temp:temp");
        agent.setAlwaysPull(false);
        agent.setReuseNode(false);

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

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
    }


    @Test
    public void dockerfileGenerationTest() {
        Agent agent = new Agent();
        agent.setDockerfile("my-dockerfile");
        agent.setArgs("-v temp:temp");

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

        String agentSnippetExpected =
                "agent {\n" +
                        "\tdockerfile {\n" +
                        "\t\tfilename 'my-dockerfile'\n" +
                        "\t\tadditionalBuildArgs '-v temp:temp'\n" +
                        "\t\treuseNode false\n" +
                        "\t}\n" +
                        "}\n";

        assertEquals(agentSnippetExpected, agentSnippetActual);
    }

    @Test
    public void dockerfileGenerationTest2() {
        Agent agent = new Agent();
        agent.setDockerfile("my-dockerfile");
        agent.setArgs("-v temp:temp");
        agent.setLabel("my-label");
        agent.setCustomWorkspace("my-customWorkspace");
        agent.setDir("dir-path");
        agent.setReuseNode(true);

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

        String agentSnippetExpected =
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
    }

    @Test
    public void dockerfileGenerationTest3() {
        Agent agent = new Agent();
        agent.setDockerfile("my-dockerfile");
        agent.setArgs("-v temp:temp");
        agent.setLabel("my-label");
        agent.setCustomWorkspace("my-customWorkspace");
        agent.setDir("dir-path");
        agent.setReuseNode(false);

        String agentSnippetActual =
                PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(agent));

        String agentSnippetExpected =
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
