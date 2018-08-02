package io.jenkins.plugins.sprp;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import io.jenkins.plugins.sprp.models.Agent;
import io.jenkins.plugins.sprp.models.Environment;
import io.jenkins.plugins.sprp.models.Post;
import jenkins.model.Jenkins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractPSG implements ExtensionPoint {

    public abstract List<String> getAgent(Agent agent);
    public abstract List<String> getTools(HashMap<String, String> tools);
    public abstract List<String> getEnvironment(Environment environment);
    public abstract List<String> getPostSection(Post postSection);
    public abstract List<String> getArchiveArtifactsStage(ArrayList<String> paths);

    public static ExtensionList<AbstractPSG> all() {
        return Jenkins.getInstance().getExtensionList(AbstractPSG.class);
    }
}
