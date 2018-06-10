package io.jenkins.plugins.sprp;

import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.plugins.git.Branch;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.jenkinsci.plugins.gitclient.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class GitOperations {
    private static final String DUMMY_BRANCH_NAME = "DUMMY_8DD2963";   // Just to avoid duplicate branch name
    private FilePath workspace;
    private TaskListener listener;
    private EnvVars envVars;
    private String url;
    private GitClient git;
    private String currentBranch;
    private String currentBranchSHA1;

    public GitOperations(FilePath workspace, TaskListener listener, EnvVars envVars, String url) throws IOException, InterruptedException {
        this.workspace = workspace;
        this.envVars = envVars;
        this.listener = listener;
        this.url = url;
        this.currentBranch = null;

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

    public void setWorkspace(FilePath workspace) throws IOException, InterruptedException {
        this.workspace = workspace;
        initialiseGitClient();
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public void setCurrentBranch(String currentBranch) {
        this.currentBranch = currentBranch;
    }

    public boolean cloneTheRepo(String branch) {
        CloneCommand clone = git.clone_();
        ArrayList<RefSpec> refSpecs = new ArrayList<>();
        clone.url(url);

        if (branch != null && !branch.equals("")) {
            refSpecs.add(new RefSpec()
                    .setSourceDestination("+refs/heads/" + branch, "refs/remotes/origin/" + branch));
            clone.refspecs(refSpecs);
        }

        try {
            clone.execute();
        } catch (InterruptedException e) {
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

        if (branch != null && !branch.equals("")) {
            rfs.add(new RefSpec().setSourceDestination(branch, branch));
        }

        fetch.prune(true);

        try {
            fetch.from(new URIish(this.url), rfs);
            fetch.execute();
        } catch (InterruptedException e) {
            listener.getLogger().println("Error while fetching branch from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        } catch (URISyntaxException e) {
            listener.getLogger().println("Invalid repository url");
            return false;
        }

        listener.getLogger().println("Fetch successful from " + url + ".");
        return checkout(branch);
    }

    public boolean checkout(String branch) {
        CheckoutCommand checkoutCommand = git.checkout();
        checkoutCommand.branch(branch);
        String tempCurrentBranchSHA1 = getObjectIdOfLocalBranch(branch).name();
        checkoutCommand.ref(currentBranchSHA1);

        try {
            checkoutCommand.execute();
        } catch (InterruptedException e) {
            listener.getLogger().println("Error while checkout to branch " + branch);
            listener.getLogger().println(e.getMessage());
            return false;
        }

        listener.getLogger().println("Cloned branch " + branch + " successfully.");
        setCurrentBranch(branch);
        currentBranchSHA1 = tempCurrentBranchSHA1;
        return true;
    }

    public boolean pullChangesOfPullrequest(int PR_Number, String branch) throws InterruptedException {
        if (!cloneTheRepo(branch))
            return false;

        FetchCommand fetchCommand = git.fetch_();

        ArrayList<RefSpec> refSpecs = new ArrayList<>();
        refSpecs.add(new RefSpec().setSourceDestination("pull/" + PR_Number + "/head", DUMMY_BRANCH_NAME));

        try {
            fetchCommand.from(new URIish(this.url), refSpecs);
            fetchCommand.execute();
        } catch (InterruptedException e) {
            listener.getLogger().println("Error while fetching from " + url);
            listener.getLogger().println(e.getMessage());
            return false;
        } catch (URISyntaxException e) {
            listener.getLogger().println("Invalid repository url");
            return false;
        }

        listener.getLogger().println("Fetched successfully.");
        printRevisions();
        if (!merge(Objects.requireNonNull(getObjectIdOfLocalBranch(DUMMY_BRANCH_NAME)).name()))
            return false;

        return deleteBranch(DUMMY_BRANCH_NAME);
    }

    public boolean merge(String rev) {
        listener.getLogger().println("Merging started with rev " + rev + ".");
        MergeCommand mergeCommand = git.merge();
        mergeCommand.setRevisionToMerge(ObjectId.fromString(rev));
        mergeCommand.setMessage("Merging to build the pull request.");
        mergeCommand.setCommit(true);

        printRevisions();

        try {
            mergeCommand.execute();
        } catch (InterruptedException e) {
            listener.getLogger().println("Error while merging.");
            listener.getLogger().println(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean deleteBranch(String branch) {
        try {
            git.deleteBranch(branch);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean push(boolean fromHead) {
        PushCommand pushCommand = git.push();

        try {
            pushCommand.to(new URIish(this.url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        // https://stackoverflow.com/a/4183856/6693569
        // At this point the pointer may be at detached HEAD
        if(fromHead)
            pushCommand.ref("HEAD:" + currentBranch);
        else
            pushCommand.ref(currentBranch);

        pushCommand.force(true);

        try {
            pushCommand.execute();
        } catch (InterruptedException e) {
            listener.getLogger().println("Error while pushing the branch " + currentBranch);
            e.printStackTrace();
            return false;
        }

        listener.getLogger().println("Branch " + currentBranch + " pushed successfully.");
        return true;
    }

    public void setUsernameAndPasswordCredential(StandardUsernameCredentials cred){
        git.setCredentials(cred);
    }

    private String extractObjectIdFromBranch(String branch) {
        String objectId;
        int branchLen = branch.length();
        objectId = branch.substring(branchLen - 41, branchLen - 1);
        return objectId;
    }

    private ObjectId getObjectIdOfLocalBranch(String branch) {
        try {
            Set<Branch> allBranches = (Set<Branch>) git.getBranches();

            ArrayList<Branch> branches = new ArrayList<Branch>();

            for (Branch b : allBranches) {
                if (b.toString().contains(branch)) {
//                    System.out.println("found: " + b.toString());
                    branches.add(b);
                }
            }

            if(branches.size() > 1) {
                allBranches.clear();
                allBranches.addAll(branches);
                branches.clear();

                for (Branch b : allBranches) {
                    if (!b.toString().contains("remotes")) {
                        System.out.println("found: " + b.toString());
                        branches.add(b);
                    }
                }
            }

            listener.getLogger().println("Number of branches: " + branches.size());

            if (branches.size() > 1) {
                listener.getLogger().println("More than one branches found containing " + branch);
                for (Branch b : branches) {
                    listener.getLogger().print("  - " + b.toString() + " : ");
                    listener.getLogger().println(extractObjectIdFromBranch(b.toString()));
                }
                return null;
            } else if (branches.size() == 1)
                return ObjectId.fromString(extractObjectIdFromBranch(branches.get(0).toString()));
            else {
                listener.getLogger().println("Cannot find a branch with name : " + branch);
                return null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            listener.getLogger().println("Error while getting ObjectId of branch : " + branch);
            return null;
        }
    }


    //TODO: This function needs to be removed before publishing.
    private void printRevisions() {
        try {
            Set<Branch> branchs = (Set<Branch>) git.getBranches();
            listener.getLogger().println("List of branches: ");
            for (Branch b : branchs) {
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
            for (String b : rfnames) {
                listener.getLogger().println("  - " + b);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            HashMap<String, ObjectId> headrevs = (HashMap<String, ObjectId>) git.getHeadRev(this.url);
            listener.getLogger().println("Head Revisions");
            for (Map.Entry<String, ObjectId> e : headrevs.entrySet()) {
                listener.getLogger().println("  - " + e.getKey() + " : " + e.getValue().name());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
