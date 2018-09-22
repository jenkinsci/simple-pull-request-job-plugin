package io.jenkins.plugins.sprp.generators;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.jenkins.plugins.sprp.models.ArtifactPublishingConfig;
import io.jenkins.plugins.sprp.models.ReportsAndArtifactsInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@For(PublishReportsAndArtifactsStageGenerator.class)
public class PublishReportsAndArtifactsStageGenaratorTest {
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private PublishReportsAndArtifactsStageGenerator generator;

    @Before
    public void setupGenerator() {
        generator = PipelineGenerator.lookupConverter(PublishReportsAndArtifactsStageGenerator.class);
    }

    @Test
    public void test1() {
        ReportsAndArtifactsInfo reportsAndArtifactsInfo = new ReportsAndArtifactsInfo();

        ArrayList<String> reports = new ArrayList<>();
        reports.add("location-of-report-1");
        reports.add("location-of-report-2");

        ArtifactPublishingConfig artifactPublishingConfig = new ArtifactPublishingConfig();
        artifactPublishingConfig.setCredentialId("credentialId");
        artifactPublishingConfig.setHost("123.123.123.123");
        artifactPublishingConfig.setUser("username");

        ArrayList<HashMap<String, String>> artifactsToArchive = new ArrayList<>();

        HashMap<String, String> singleArtifactToArchive1 = new HashMap<>();
        singleArtifactToArchive1.put("from", "from-path-1");
        singleArtifactToArchive1.put("to", "to-path-1");

        artifactsToArchive.add(singleArtifactToArchive1);

        HashMap<String, String> singleArtifactToArchive2 = new HashMap<>();
        singleArtifactToArchive2.put("from", "from-path-2");
        singleArtifactToArchive2.put("to", "to-path-2");
        artifactsToArchive.add(singleArtifactToArchive2);


        reportsAndArtifactsInfo.setReports(reports);
        reportsAndArtifactsInfo.setArtifactPublishingConfig(artifactPublishingConfig);
        reportsAndArtifactsInfo.setPublishArtifacts(artifactsToArchive);

        String pipelineStageActual = PipelineGenerator.autoAddTabs((ArrayList<String>) generator.toPipeline(reportsAndArtifactsInfo));
        String pipelineStageExpected =
                "stage('Publish reports & artifacts') {\n" +
                "\tsteps {\n" +
                "\t\tjunit 'location-of-report-1'\n" +
                "\t\tjunit 'location-of-report-2'\n" +
                "\t\twithCredentials([file(credentialsId: 'credentialId', variable: 'FILE')]) {\n" +
                "\t\t\tsh 'scp -i $FILE from-path-1 username@123.123.123.123:to-path-1'\n" +
                "\t\t\tsh 'scp -i $FILE from-path-2 username@123.123.123.123:to-path-2'\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}\n";

        assertEquals(pipelineStageExpected, pipelineStageActual);
    }
}
