package repo.mining.bugs;

import com.jcabi.github.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

public class Controller {

    // TODO: issues and PRs linked via 'connected' event (by Development part on the right) are not included; the GitHub API does not seem to include information to what it is linked

    private final Repo repo;
    private final ProgressLogger logger = new ProgressLogger(true);
    private final Map<String, Integer> bugFiles = new HashMap<>();

    private final Github github;

    public Controller(Repo repo, Github github) {
        this.repo = repo;
        this.github = github;
    }

    public Map<String, Integer> getBugFiles() {
        return bugFiles;
    }

    public void findBugFiles(){
        // We need to explicitly indicate that we only want closed issues; otherwise, only open issues are retrieved.
        EnumMap<Issues.Qualifier, String> params = new EnumMap<>(Issues.Qualifier.class);
        params.put(Issues.Qualifier.STATE, "closed");
        Iterable<Issue> issues = repo.issues().search(Issues.Sort.CREATED, Search.Order.ASC, params);

        for (Issue issue : issues) {
            try {
                if (isLabelledAsBug(issue) && isCompleted(issue) && !(new Issue.Smart(issue)).isPull()) { // Issues can be issues or PRs; here we only want issues.
                    logger.log("Bug issue " + issue.number());
                    findBugFilesFromIssue(issue, repo.pulls(), repo.commits());
                }
            } catch (IOException ignored) {

            }
        }
    }

    private boolean isLabelledAsBug(Issue issue){
        for (Label label : issue.labels().iterate()) {
            if (label.name().toLowerCase().contains("bug")) {
                return true;
            }
        }

        return false;
    }

    private boolean isCompleted(Issue issue){
        try {
            return issue.json().getString("state_reason").equals("completed");
        } catch (IOException e) {
            return false;
        }
    }

    private void findBugFilesFromIssue(Issue issue, Pulls pullRequests, RepoCommits commits){
        try {
            JsonObject json = issue.json();

            // We have to use timeline here instead of events,
            // because the 'cross-referenced' event is only included in the timeline.
            Timeline timeline = new Timeline(json.getString("timeline_url"), repo.json().getInt("id"), github);

            // Since different projects organize their issues and pull requests in different ways,
            // we try to retrieve bugs both from linked pull requests and from linked commits.
            findBugFilesFromPullRequests(timeline.getLinkedPullRequestIds(), pullRequests);
            findBugFilesFromCommits(timeline.getLinkedCommits(), commits);
        } catch (IOException ignored) {

        }
    }

    private void findBugFilesFromPullRequests(List<Integer> pullRequestIds, Pulls pullRequests){
        for (int pullRequestId : pullRequestIds) {
            Pull pullRequest = pullRequests.get(pullRequestId);
            try {
                Pull.Smart smartPullRequest = new Pull.Smart(pullRequest);
                // We only consider closed and merged PRs, because they are more likely to be actual fixes.
                if (smartPullRequest.state().equals("closed") && smartPullRequest.json().getBoolean("merged")) {
                    findBugFilesFromPullRequest(pullRequest);
                }
            } catch (IOException ignored) {

            }
        }
    }

    private void findBugFilesFromPullRequest(Pull pullRequest){
        try {
            for (JsonObject fileJsonObject : pullRequest.files()){
                String filename = fileJsonObject.getString("filename");
                bugFiles.merge(filename, 1, Integer::sum);
                logger.log("File " + filename + " changed via PR " + pullRequest.number());
            }
        } catch (IOException ignored) {

        }
    }

    private void findBugFilesFromCommits(List<String> commitHashes, RepoCommits commits){
        for (String commitHash : commitHashes){
            RepoCommit commit = commits.get(commitHash);
            findBugFilesFromCommit(commit);
        }
    }

    private void findBugFilesFromCommit(RepoCommit commit){
        try {
            JsonArray jsonFiles = commit.json().getJsonArray("files");
            for (JsonValue jsonFile : jsonFiles){
                String filename = jsonFile.asJsonObject().getString("filename");
                bugFiles.merge(filename, 1, Integer::sum);
                logger.log("File " + filename + " changed via commit " + commit.sha());
            }
        } catch (IOException ignored) {

        }
    }

    public void close(){
        logger.close();
    }

}
