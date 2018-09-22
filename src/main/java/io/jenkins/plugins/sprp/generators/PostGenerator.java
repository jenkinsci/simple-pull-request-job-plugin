package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import io.jenkins.plugins.sprp.exception.ConversionException;
import io.jenkins.plugins.sprp.models.Post;
import io.jenkins.plugins.sprp.models.Step;
import org.jenkinsci.Symbol;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Extension
@Symbol("post")
public class PostGenerator extends PipelineGenerator<Post> {

    @Nonnull
    @Override
    public List<String> toPipeline(Post postSection) throws ConversionException {
        ArrayList<String> snippetLines = new ArrayList<>();

        if (postSection == null) {
            return snippetLines;
        }

        snippetLines.add("post {");

        snippetLines.addAll(getPostConditionSnippetIfNonNull("always", postSection.getAlways()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("changed", postSection.getChanged()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("fixed", postSection.getFixed()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("regression", postSection.getRegression()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("aborted", postSection.getAborted()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("failure", postSection.getFailure()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("success", postSection.getSuccess()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("unstable", postSection.getUnstable()));
        snippetLines.addAll(getPostConditionSnippetIfNonNull("cleanup", postSection.getCleanup()));

        snippetLines.add("}");

        return snippetLines;
    }

    @Override
    public boolean canConvert(@Nonnull Object object) {
        return object instanceof Post;
    }

    private List<String> getPostConditionSnippetIfNonNull(String postCondition, ArrayList<LinkedHashMap<String, Step>> steps)
            throws ConversionException {
        ArrayList<String> snippetLines = new ArrayList<>();
        if (steps != null) {
            snippetLines.add(postCondition + " {");

            for (LinkedHashMap<String, Step> step : steps) {
                for (Map.Entry<String, Step> entry : step.entrySet()) {
                    snippetLines.addAll(PipelineGenerator.convert("step", entry.getValue()));
                }
            }

            snippetLines.add("}");
        }

        return snippetLines;
    }
}
