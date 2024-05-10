package repo.mining.bugs.elements;

import javax.json.JsonObject;
import java.io.IOException;

public class Event {
    private final JsonObject jsonObject;
    private String event;
    private PullRequest pullRequest;
    private Commit commit;
    private int repoId;

    public Event(JsonObject jsonObject) throws IOException {
        this.jsonObject = jsonObject;
        parseJson();
    }

    private void parseJson() throws IOException {
        this.event = jsonObject.getString("event");

        if (jsonObject.containsKey("source")){
            JsonObject issue = jsonObject.getJsonObject("source").getJsonObject("issue");
            this.repoId = issue.getJsonObject("repository").getInt("id");
        }
    }

    public PullRequest getPullRequest() throws IOException {
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

    public Commit getCommit() throws IOException {
        if (commit == null){
            if (jsonObject.containsKey("commit_url") && !jsonObject.isNull("commit_url")){
                this.commit = new Commit(jsonObject.getString("commit_url"));
            }
        }
        return commit;
    }

    public String getEvent() {
        return event;
    }

    public boolean sameRepo(Repo repo){
        return repo.getId() == this.repoId;
    }

}
