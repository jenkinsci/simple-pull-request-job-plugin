package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.exception.ConversionException;
import io.jenkins.plugins.sprp.models.Step;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@For(StepGenerator.class)
public class StepGeneratorTest {
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private StepGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(StepGenerator.class);
    }

    @Test
    public void sh() throws ConversionException {
        Step step = new Step();
        step.setStepName("sh");
        step.setDefaultParameter("build.sh");

        String pipelineStepActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(step));
        String pipelineStepExpected = "sh 'build.sh'\n";

        assertEquals(pipelineStepExpected, pipelineStepActual);
    }

    @Test
    public void sleep() throws ConversionException {
        Step step = new Step();
        HashMap<String, Object> parameters = new HashMap<>();

        step.setStepName("sleep");

        parameters.put("time", 2);

        step.setParameters(parameters);


        String pipelineStepActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(step));
        String pipelineStepExpected = "sleep 2\n";

        assertEquals(pipelineStepExpected, pipelineStepActual);
    }
}
