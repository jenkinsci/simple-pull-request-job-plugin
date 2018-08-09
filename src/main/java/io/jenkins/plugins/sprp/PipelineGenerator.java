package io.jenkins.plugins.sprp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jenkinsci.plugins.structs.SymbolLookup;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
public abstract class PipelineGenerator<T> implements ExtensionPoint {
    static private Logger logger = Logger.getLogger(PipelineGenerator.class.getClass().getName());

    @Nonnull
    public abstract List<String> toPipeline(@CheckForNull T object) throws ConversionException;

    public abstract boolean canConvert(@Nonnull Object object);

    public static ExtensionList<PipelineGenerator> all() {
        return ExtensionList.lookup(PipelineGenerator.class);
    }

    @CheckForNull
    public static PipelineGenerator lookupForName(@Nonnull String name) {
        return SymbolLookup.get().find(PipelineGenerator.class, name);
    }

    @CheckForNull
    public static <T extends PipelineGenerator> T lookupConverter(Class<T> clazz) {
        for (PipelineGenerator gen : all()) {
            if (clazz.equals(gen.getClass())) {
                return clazz.cast(gen);
            }
        }
        return null;
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    @Nonnull
    public static <T extends PipelineGenerator> T lookupConverterOrFail(Class<T> clazz)
            throws ConversionException {
        T converter = lookupConverter(clazz);
        if (converter == null) {
            throw new ConversionException("Failed to find converter: " + clazz);
        }
        return converter;
    }

    @CheckForNull
    public static PipelineGenerator lookup(@Nonnull Object object) {
        for (PipelineGenerator gen : all()) {
            if (gen.canConvert(object)) {
                return gen;
            }
        }
        return null;
    }

    @Nonnull
    public static List<String> convert(@Nonnull Object object) throws ConversionException {
        PipelineGenerator gen = lookup(object);
        if (gen == null) {
            // TODO: add better diagnostics (field matching)
            throw new ConversionException("Cannot find converter for the object: " + object.getClass());
        }
        //TODO: handle raw type conversion risks
        return gen.toPipeline(object);
    }

    @Nonnull
    public static List<String> convert(@Nonnull String converterName, @CheckForNull Object object) throws ConversionException {
        PipelineGenerator gen = lookupForName(converterName);
        if (gen == null) {
            // TODO: add better diagnostics (field matching)
            throw new ConversionException("Cannot find converter for the type: " + converterName);
        }
        //TODO: handle raw type conversion risks
        return gen.toPipeline(object);
    }

    public static String autoAddTabs(ArrayList<String> snippetLines) {
        int numOfTabs = 0;
        StringBuilder snippet = new StringBuilder();

        for (String str : snippetLines) {
            if (str.startsWith("}")) {
                numOfTabs--;
            }

            if (numOfTabs != 0) {
                snippet.append(StringUtils.repeat("\t", numOfTabs));
            }

            snippet.append(str).append("\n");

            if (str.endsWith("{")) {
                numOfTabs++;
            }
        }

        return snippet.toString();
    }
}
