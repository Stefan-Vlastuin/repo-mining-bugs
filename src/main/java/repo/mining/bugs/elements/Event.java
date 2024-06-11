package repo.mining.bugs.elements;

import javax.json.JsonObject;
import java.io.IOException;

public class Event {
    private final JsonObject jsonObject;
    private String event;
    private PullRequest pullRequest;
    private Commit commit;
    private String commitUrl;
    private int repoId;

    public Event(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        parseJson();
    }

    private void parseJson() {
        this.event = jsonObject.getString("event");

        if (jsonObject.containsKey("source")){
            JsonObject issue = jsonObject.getJsonObject("source").getJsonObject("issue");
            this.repoId = issue.getJsonObject("repository").getInt("id");
        }

        if (jsonObject.containsKey("commit_url") && !jsonObject.isNull("commit_url")){
            this.commitUrl = jsonObject.getString("commit_url");
        }
    }

    public PullRequest getPullRequest() throws IOException, InterruptedException {
        if (pullRequest == null) {
            if (jsonObject.containsKey("source")){
                JsonObject issue = jsonObject.getJsonObject("source").getJsonObject("issue");
                if (issue.containsKey("pull_request")){
                    this.pullRequest = new PullRequest(issue.getJsonObject("pull_request").getString("url"));
                }
            }
        }
        return pullRequest;
    }

    public Commit getCommit() throws IOException, InterruptedException {
        if (commit == null){
            if (commitUrl != null){
                this.commit = new Commit(commitUrl);
            }
        }
        return commit;
    }

    public String getEvent() {
        return event;
    }

    public boolean sameRepo(Repo repo, boolean checkCommit){
        if (checkCommit){
            if (commitUrl == null){
                return false;
            }
            // For a commit, we do not have the repoId, so we check on repo owner and name
            return commitUrl.contains(repo.getOwner() + "/" + repo.getName());
        }

        return repo.getId() == this.repoId;
    }

    public boolean sameRepo(Repo repo){
        return sameRepo(repo, false);
    }

}
