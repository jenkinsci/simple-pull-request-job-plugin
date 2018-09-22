package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.exception.ConversionException;
import io.jenkins.plugins.sprp.YamlToPipeline;
import org.apache.tools.ant.filters.StringInputStream;
import org.hamcrest.CoreMatchers;
import org.jenkinsci.Symbol;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Oleg Nenashev
 */
@For(CustomSectionGenerator.class)
public class CustomSectionGeneratorTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void checkCustomSection() throws Exception {
        String yaml = "agent: any\n" +
                "\n" +
                "stages:\n" +
                "  - name: stage1\n" +
                "    agent: any\n" +
                "    steps:\n" +
                "      - sh: \"scripts/hello.sh\"\n" +
                "\n" +
                "configuration:\n" +
                "  sections:\n" +
                "    - name: foo\n" +
                "      data:\n" +
                "        field1: hello\n" +
                "        field2: world\n" +
                "\n";

        StringInputStream yamlInputStream = new StringInputStream(yaml);
        String generatedPipeline = new YamlToPipeline().generatePipeline(yamlInputStream, null,
                j.createTaskListener());
        Assert.assertThat(generatedPipeline, CoreMatchers.containsString("foo('hello','world')"));
    }

    @TestExtension
    @Symbol("foo")
    public static class FooGenerator extends PipelineGenerator {

        @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
        @Nonnull
        @Override
        public List<String> toPipeline(@CheckForNull Object object) throws ConversionException {
            if (object == null) {
                throw new ConversionException("No data");
            }
            HashMap<String, String> data = (HashMap<String, String>)object;
            return Collections.singletonList("foo('" + data.get("field1") +"','" + data.get("field2") + "')");
        }

        @Override
        public boolean canConvert(@Nonnull Object object) {
            return true;
        }
    }
}
