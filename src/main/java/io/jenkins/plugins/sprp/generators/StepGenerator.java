package io.jenkins.plugins.sprp.generators;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.sprp.ConversionException;
import io.jenkins.plugins.sprp.PipelineGenerator;
import io.jenkins.plugins.sprp.models.Step;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.casc.Configurator;
import org.jenkinsci.plugins.casc.ConfiguratorException;
import org.jenkinsci.plugins.casc.model.Mapping;
import org.jenkinsci.plugins.casc.model.Scalar;
import org.jenkinsci.plugins.casc.model.Sequence;
import org.jenkinsci.plugins.workflow.cps.Snippetizer;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Extension
@Symbol("step")
public class StepGenerator extends PipelineGenerator<Step> {

    @Nonnull
    @Override
    public List<String> toPipeline(Step step) throws ConversionException {
        ArrayList<String> pipelineStep = new ArrayList<>();
        pipelineStep.add(stepConfigurator(step));
        return pipelineStep;
    }

    @Override
    public boolean canConvert(@Nonnull Object object) {
        return object instanceof Step;
    }

    private String stepConfigurator(Step step) throws ConversionException {
        if (step == null)
            return "\n";

        String snippet;
        Object stepObject;
        Descriptor<org.jenkinsci.plugins.workflow.steps.Step> stepDescriptor = StepDescriptor.byFunctionName(step.getStepName());

        if (stepDescriptor == null) {
            throw new ConversionException("No step exist with the name " + step.getStepName());
        }

        Class clazz = stepDescriptor.clazz;

        if (step.getDefaultParameter() != null) {
            Constructor constructor = Configurator.getDataBoundConstructor(clazz);

            if (constructor != null && constructor.getParameterCount() == 1) {
                try {
                    stepObject = constructor.newInstance(step.getDefaultParameter());
                } catch (InvocationTargetException e) {
                    throw new ConversionException("Error while invoking constructor " + constructor.getName() +
                            " with parameter type " + constructor.getParameters()[0].getType(), e);
                } catch (InstantiationException e) {
                    throw new ConversionException("Error while instantiating" + step.getStepName() +
                            " step object with constructor  " + constructor.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new ConversionException("Unknown error while instantiating an object of step " +
                            step.getStepName() + " with default parameter");
                }
            } else {
                throw new ConversionException("No suitable constructor found for default parameter of step "
                        + step.getStepName());
            }
        } else {
            Mapping mapping = doMappingForMap(step.getParameters());

            Configurator configurator = Configurator.lookup(clazz);

            if (configurator != null) {
                try {
                    stepObject = configurator.configure(mapping);
                } catch (ConfiguratorException e) {
                    throw new ConversionException("JCasC plugin is not able to configure the step + " + step.getStepName(), e);
                }
            } else {
                throw new ConversionException("No step with name '" + step.getStepName() +
                        "' exist. Have you installed required plugin.");
            }
        }

        snippet = Snippetizer.object2Groovy(stepObject);
        return snippet;
    }

    private Mapping doMappingForMap(Map<String, Object> map) throws ConversionException {
        Mapping mapping = new Mapping();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                mapping.put(entry.getKey(), doMappingForMap((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof List) {
                mapping.put(entry.getKey(), doMappingForSequence((List) entry.getValue()));
            } else {
                mapping.put(entry.getKey(), doMappingForScalar(entry.getValue()));
            }
        }

        return mapping;
    }

    private Scalar doMappingForScalar(Object object) throws ConversionException {
        Scalar scalar;

        if (object instanceof String) {
            scalar = new Scalar((String) object);
        } else if (object instanceof Number) {
            scalar = new Scalar((Number) object);
        } else if (object instanceof Enum) {
            scalar = new Scalar((Enum) object);
        } else if (object instanceof Boolean) {
            scalar = new Scalar((Boolean) object);
        } else {
            throw new ConversionException(object.getClass() + " is not supported.");
        }

        return scalar;
    }

    private Sequence doMappingForSequence(List<Object> objects) throws ConversionException {
        Sequence sequence = new Sequence();

        for (Object object : objects) {
            if (object instanceof Map) {
                sequence.add(doMappingForMap((Map<String, Object>) object));
            } else if (object instanceof Sequence) {
                sequence.add(doMappingForSequence((List) object));
            } else {
                sequence.add(doMappingForScalar(object));
            }
        }

        return sequence;
    }
}
