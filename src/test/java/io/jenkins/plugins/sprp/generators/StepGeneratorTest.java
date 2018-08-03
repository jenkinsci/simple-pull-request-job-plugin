package io.jenkins.plugins.sprp.generators;

import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Step;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@For(StepGenerator.class)
public class StepGeneratorTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private StepGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(StepGenerator.class);
    }

    @Test
    public void echo() throws IllegalAccessException, InvocationTargetException, ConfiguratorException, InstantiationException, NoSuchMethodException, NotSupportedException {
        Step step = new Step();
        step.setStepName("sh");
        step.setDefaultParameter("build.sh");

        String pipelineStepActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(step));
        String pipelineStepExpected = "sh 'build.sh'\n";

        assertEquals(pipelineStepExpected, pipelineStepActual);
    }

    // Todo: Add more complex step tests;
}
