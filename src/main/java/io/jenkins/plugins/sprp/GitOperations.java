package io.jenkins.plugins.sprp;

import hudson.EnvVars;
import hudson.model.TaskListener;
import hudson.triggers.SCMTrigger;
import org.codehaus.groovy.runtime.callsite.DummyCallSite;
import hudson.plugins.git.Branch;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class GitOperations {
    File workspace;
    TaskListener listener;
    EnvVars envVars;
    String url;
    GitClient git;

    final String DUMMY_BRANCH_NAME = "DUMMY_8DD2963";   // Just to avoid duplicate branch name

    public GitOperations(File workspace, TaskListener listener, EnvVars envVars, String url) throws IOException, InterruptedException {
        this.workspace = workspace;
        this.envVars = envVars;
        this.listener = listener;
        this.url = url;

        initialiseGitClient();
    }

    private void initialiseGitClient() throws IOException, InterruptedException {
        git = Git.with(listener, envVars)
                .in(workspace)
                .using("git")
                .getClient();
    }

    public void setEnvVars(EnvVars envVars) throws IOException, InterruptedException {
        this.envVars = envVars;
        initialiseGitClient();
    }

    public TaskListener getListener() {
        return listener;
    }

    public void setWorkspace(File workspace) throws IOException, InterruptedException {
        this.workspace = workspace;
        initialiseGitClient();
    }

    public boolean cloneTheRepo(String branch) {
        CloneCommand clone = git.clone_();
        ArrayList<RefSpec> refSpecs = new ArrayList<>();;
        clone.url(url);

//        if (branch != null && !branch.equals("")){
//
//            refSpecs.add(new RefSpec().setSourceDestination(branch, branch));
//            clone.refspecs(refSpecs);
//        }

        try{
            clone.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while cloning branch " + branch + "from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        }

        listener.getLogger().println("Cloned branch " + branch + " successfully from " + url + ".");
        return checkout(branch);
    }

    public boolean fetch(String branch, List<RefSpec> refSpecs) {
        FetchCommand fetch = git.fetch_();
        ArrayList<RefSpec> rfs = new ArrayList<>();

        if (branch != null && !branch.equals("")){
            rfs.add(new RefSpec().setSourceDestination(branch, branch));
        }



        fetch.prune(true);

        try{
            fetch.from(new URIish(this.url), rfs);
            fetch.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while fetching branch from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        }catch (URISyntaxException e){
            listener.getLogger().println("Invalid repository url");
            return false;
        }

        listener.getLogger().println("Fetch successful from " + url + ".");
        return checkout(branch);
    }

    public boolean checkout(String branch) {
//        CheckoutCommand checkoutCommand = git.checkout();
//        checkoutCommand.branch(branch);
//
//
//        try {
//            checkoutCommand.execute();
//        }catch (InterruptedException e){
//            listener.getLogger().println("Error while checkout to branch " + branch);
//            listener.getLogger().println(e.getMessage());
//            return false;
//        }
//
//        listener.getLogger().println("Cloned branch " + branch + " successfully.");
        try {
            git.checkout(branch);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean pullChangesOfPullrequest(int PR_Number, String branch) throws InterruptedException {
        if(!cloneTheRepo(branch))
            return false;

        FetchCommand fetchCommand = git.fetch_();

        ArrayList<RefSpec> refSpecs = new ArrayList<>();
        refSpecs.add(new RefSpec().setSourceDestination("pull/" + PR_Number + "/head", DUMMY_BRANCH_NAME));

        try{
            fetchCommand.from(new URIish(this.url), refSpecs);
            fetchCommand.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while fetching from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        }catch (URISyntaxException e){
            listener.getLogger().println("Invalid repository url");
            return false;
        }

        listener.getLogger().println("Fetched successfully.");
        printRevisions();
        if(!merge(getObjectIdOfBranch(DUMMY_BRANCH_NAME).name()))
            return false;

        return checkout(branch);
    }

    public boolean merge(String rev) throws InterruptedException {
        listener.getLogger().println("Merging started with rev " + rev + ".");
        MergeCommand mergeCommand = git.merge();
        mergeCommand.setRevisionToMerge(ObjectId.fromString(rev));
        mergeCommand.setMessage("Merging to build the pull request.");
        mergeCommand.setCommit(true);

        printRevisions();

        try{
            mergeCommand.execute();
        }catch (InterruptedException e){
            listener.getLogger().println("Error while merging.");
            listener.getLogger().println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean deleteBranch(String branch){
        try {
            git.deleteBranch(branch);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String extractObjectIdFromBranch(String branch){
        String objectId;
        int branchLen = branch.length();
        objectId = branch.substring(branchLen - 41, branchLen - 1);
        return objectId;
    }

    private ObjectId getObjectIdOfBranch(String branch){
        try {
            ArrayList<Branch> branches = (ArrayList<Branch>) git.getBranchesContaining(branch,false);
            listener.getLogger().println("Number of branches: " + branch.length());
            if(branches.size() > 1) {
                listener.getLogger().println("Branch length is greater than 1");
                for (Branch b: branches) {
                    listener.getLogger().print("  - " + b.toString() + " : ");
                    listener.getLogger().println(extractObjectIdFromBranch(b.toString()));
                }
                return null;
            }

            for (Branch b: branches) {
                return ObjectId.fromString(extractObjectIdFromBranch(b.toString()));
            }
            listener.getLogger().println("Cannot find a branch with name : " + branch);
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            listener.getLogger().println("Error while getting ObjectId of branch : " + branch);
            return null;
        }
    }

    private void printRevisions(){
        try {
            ArrayList<Branch> branchs = (ArrayList<Branch>) git.getBranchesContaining(DUMMY_BRANCH_NAME,false);
            listener.getLogger().println("List of branches: ");
            for (Branch b: branchs) {
                listener.getLogger().print("  - " + b.toString() + " : ");
                listener.getLogger().println(extractObjectIdFromBranch(b.toString()));
            }

            listener.getLogger().println("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Set<String> rfnames = git.getRefNames("");
            listener.getLogger().println("List of references: ");
            for (String b: rfnames) {
                listener.getLogger().println("  - " + b);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            HashMap<String, ObjectId> headrevs = (HashMap<String, ObjectId>) git.getHeadRev(this.url);
            listener.getLogger().println("Head Revisions");
            for (Map.Entry<String, ObjectId> e:headrevs.entrySet()) {
                listener.getLogger().println("  - " + e.getKey() + " : " + e.getValue().name());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
