package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import io.jenkins.plugins.sprp.ConversionException;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Agent;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.Step;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.casc.ConfiguratorException;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Extension
@Symbol("stage")
public class StageGenerator extends PipelineGenerator<Stage> {

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    @Nonnull
    @Override
    public List<String> toPipeline(Stage stage) throws ConversionException, IllegalAccessException, InvocationTargetException, ConfiguratorException, InstantiationException, NoSuchMethodException, NotSupportedException {
        String stageName = stage.getName();

        ArrayList<String> snippetLines = new ArrayList<>();
        snippetLines.add("stage('" + stageName + "') {");

        final Agent agent = stage.getAgent();
        if (agent != null && !agent.getAnyOrNone().equals("any")) {
            AgentGenerator gen = lookupConverterOrFail(AgentGenerator.class);
            snippetLines.addAll(gen.toPipeline(agent));
        }

        snippetLines.add("steps {");

        for (LinkedHashMap<String, Step> step : stage.getSteps()) {
            for (Map.Entry<String, Step> entry : step.entrySet()) {
                snippetLines.addAll(PipelineGenerator.convert("step", entry.getValue()));
            }
        }

        snippetLines.add("}");

        snippetLines.addAll(PipelineGenerator.convert("post", stage.getPost()));

        snippetLines.add("}");

        return snippetLines;
    }

    @Override
    public boolean canConvert(@Nonnull Object object) {
        return object instanceof Stage;
    }
}
