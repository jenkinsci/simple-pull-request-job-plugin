package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import io.jenkins.plugins.sprp.ConversionException;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.CustomPipelineSection;
import org.eclipse.jgit.errors.NotSupportedException;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.casc.ConfiguratorException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

/**
 * Converter for {@link CustomPipelineSection}.
 * @author Oleg Nenashev
 */
@Extension
@Symbol("custom")
public class CustomSectionGenerator extends PipelineGenerator<CustomPipelineSection> {

    @SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
    @Nonnull
    @Override
    public List<String> toPipeline(@CheckForNull CustomPipelineSection section)
            throws ConversionException, IllegalAccessException, ConfiguratorException, InstantiationException, NotSupportedException, NoSuchMethodException, InvocationTargetException {
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
