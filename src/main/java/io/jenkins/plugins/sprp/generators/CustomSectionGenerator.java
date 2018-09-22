package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import io.jenkins.plugins.sprp.exception.ConversionException;
import io.jenkins.plugins.sprp.models.CustomPipelineSection;
import org.jenkinsci.Symbol;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Converter for {@link CustomPipelineSection}.
 * @author Oleg Nenashev
 */
@Extension
@Symbol("custom")
public class CustomSectionGenerator extends PipelineGenerator<CustomPipelineSection> {

    @Nonnull
    @Override
    public List<String> toPipeline(@CheckForNull CustomPipelineSection section)
            throws ConversionException {
        if (section == null) {
            return Collections.emptyList();
        }

        PipelineGenerator gen = PipelineGenerator.lookupForName(section.getName());
        if (gen == null) {
            throw new ConversionException("No converter for Custom Pipeline Section: " + section.getName());
        }
        return gen.toPipeline(section.getData());
    }

    @Override
    public boolean canConvert(@Nonnull Object object) {
        return object instanceof CustomPipelineSection;
    }
}
