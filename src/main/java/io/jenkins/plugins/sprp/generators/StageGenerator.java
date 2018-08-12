package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import io.jenkins.plugins.sprp.ConversionException;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Agent;
import io.jenkins.plugins.sprp.models.Stage;
import io.jenkins.plugins.sprp.models.Step;
import org.jenkinsci.Symbol;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Extension
@Symbol("stage")
public class StageGenerator extends PipelineGenerator<Stage> {

    @Nonnull
    @Override
    public List<String> toPipeline(Stage stage) throws ConversionException {
        ArrayList<String> snippetLines = new ArrayList<>();

        if(stage == null){
            return snippetLines;
        }

        String stageName = stage.getName();

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
