package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import io.jenkins.plugins.sprp.models.Credential;
import io.jenkins.plugins.sprp.models.Environment;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Extension
@Symbol("environment")
@Restricted(NoExternalUse.class)
public class EnvironmentGenerator extends PipelineGenerator<Environment> {

    @Nonnull
    @Override
    public List<String> toPipeline(Environment environment) {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (environment == null || (environment.getVariables() == null && environment.getCredentials() == null)) {
            return snippetLines;
        }

        snippetLines.add("environment {");

        for (Map.Entry<String, String> entry : environment.getVariables().entrySet()) {
            snippetLines.add(entry.getKey() + " = '" + entry.getValue() + "'");
        }

        for (Credential credential : environment.getCredentials()) {
            snippetLines.add(credential.getVariable() + " = credentials('" + credential.getCredentialId() + "')");
        }

        snippetLines.add("}");

        return snippetLines;
    }

    @Override
    public boolean canConvert(@Nonnull Object object) {
        return object instanceof Environment;
    }
}
