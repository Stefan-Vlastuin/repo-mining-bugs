package repo.mining.bugs;

import com.jcabi.github.Github;
import com.jcabi.github.Limit;
import com.jcabi.github.Repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.*;

public class Timeline {
    JsonArray jsonArray;
    //Repo repo;
    int repoId;
    Github github;

    public Timeline (String pathJson, int repoId, Github github){
        Connector connector = new Connector(pathJson);
        jsonArray = connector.getJsonArray();
        connector.close();
        //this.repo = repo;
        this.repoId = repoId;
        this.github = github;
    }

    public List<Integer> getLinkedPullRequestIds(){
        List<Integer> pullRequestIds = new ArrayList<>();

        for (JsonValue eventJsonValue : jsonArray){
            JsonObject eventJsonObject = eventJsonValue.asJsonObject();
            if (eventJsonObject.getString("event").equals("cross-referenced")
                    && isPullRequest(eventJsonObject)
                    && sameRepo(eventJsonObject)){
                pullRequestIds.add(getPullRequestId(eventJsonObject));
            }
        }

        return pullRequestIds;
    }

    private int getPullRequestId(JsonObject event){
        return event.getJsonObject("source").getJsonObject("issue").getInt("number");
    }

    private int getRepoId(JsonObject event){
        return event.getJsonObject("source").getJsonObject("issue").getJsonObject("repository").getInt("id");
    }

    private boolean sameRepo(JsonObject event){
        //try {
            //return getRepoId(event) == repo.json().getInt("id");
            return getRepoId(event) == repoId;
//        } catch (IOException e) {
//            return false;
//        }
    }

    private boolean isPullRequest(JsonObject event){
        return event.getJsonObject("source").getJsonObject("issue").containsKey("pull_request");
    }

    public List<String> getLinkedCommits(){
        List<String> commits = new ArrayList<>();

        for (JsonValue eventJsonValue : jsonArray){
            JsonObject eventJsonObject = eventJsonValue.asJsonObject();
            if (closedByCommit(eventJsonObject)){
                // TODO: technically, the commit could be in another repo
                commits.add(eventJsonObject.getString("commit_id"));
            }
        }

        return commits;
    }

    private boolean closedByCommit(JsonObject event){
        return event.getString("event").equals("closed")
                && event.containsKey("commit_id")
                && !event.isNull("commit_id");
    }

}
