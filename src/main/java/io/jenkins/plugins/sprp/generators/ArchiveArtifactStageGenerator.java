package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import io.jenkins.plugins.sprp.PipelineGenerator;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.casc.ConfiguratorException;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Extension
@Symbol("archiveArtifactStage")
public class ArchiveArtifactStageGenerator extends PipelineGenerator<ArrayList<String>> {
    @Nonnull
    @Override
    public List<String> toPipeline(ArrayList<String> paths) throws IllegalAccessException, ConfiguratorException, InstantiationException, NotSupportedException, NoSuchMethodException, InvocationTargetException {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (paths == null) {
            return snippetLines;
        }

        snippetLines.add("stage('Archive artifacts') {");
        snippetLines.add("steps {");

        for (String p : paths) {
            snippetLines.add("archiveArtifacts artifacts: '" + p + "'");
        }

        snippetLines.add("}");
        snippetLines.add("}");

        return snippetLines;
    }

    @Override
    public boolean canConvert(@Nonnull Object object) {
        if(object instanceof ArrayList<?>){
            return  ((ArrayList) object).get(0) instanceof String;
        }

        return false;
    }
}
