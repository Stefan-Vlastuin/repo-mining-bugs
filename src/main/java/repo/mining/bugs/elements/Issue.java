package repo.mining.bugs.elements;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Issue {

    private final JsonObject jsonObject;
    private int number;
    private Labels labels;
    private String state;
    private String stateReason;
    private Timeline timeline;
    private boolean isPullRequest;

    public Issue(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        parseJson();
    }

    private void parseJson(){
        this.number = jsonObject.getInt("number");
        this.labels = new Labels(jsonObject.getJsonArray("labels"));
        this.state = jsonObject.getString("state");
        this.isPullRequest = jsonObject.containsKey("pull_request");

        if (!jsonObject.isNull("state_reason")){
            this.stateReason = jsonObject.getString("state_reason");
        }
    }

    public boolean isBugReport(){
        return labels.containsSubstring("bug");
    }

    public boolean isCompleted(){
        if (stateReason == null){
            return false;
        }
        return state.equals("closed") && stateReason.equals("completed");
    }

    public boolean isPullRequest(){
        return isPullRequest;
    }

    public List<File> getChangedFiles(Repo repo) throws IOException {
        if (timeline == null){
            timeline = new Timeline(jsonObject.getString("timeline_url"));
        }

        List<File> changedFiles = new ArrayList<>();

        // Method 1: via pull requests
        for (PullRequest pullRequest : timeline.getLinkedPullRequests(repo)){
            // We only consider closed and merged PRs, because they are more likely to be actual fixes.
            if (pullRequest.isClosed() && pullRequest.isMerged()) {
                changedFiles.addAll(pullRequest.getChangedFiles().getFiles());
            }
        }

        // Method 2: via commits
        Commit commit = timeline.getClosingCommit();
        if (commit != null){
            changedFiles.addAll(commit.getChangedFiles().getFiles());
        }

        return changedFiles;
    }

    public int getNumber(){
        return number;
    }

    @Override
    public String toString() {
        return "Issue " + number;
    }

}
