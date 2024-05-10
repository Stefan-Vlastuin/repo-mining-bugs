package repo.mining.bugs;

import repo.mining.bugs.elements.File;
import repo.mining.bugs.elements.Issue;
import repo.mining.bugs.elements.Issues;
import repo.mining.bugs.elements.Repo;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewController {
//    private final static String REPO_OWNER  = "Stefan-Vlastuin";
//    private final static String REPO_NAME = "TestRepo";
    private final static String REPO_OWNER  = "javaparser";
    private final static String REPO_NAME = "javaparser";
    private final Repo repo;

    public NewController() throws IOException {
        repo = new Repo(REPO_OWNER, REPO_NAME);
    }

    public Map<String, Integer> findBugs(String bugLabel) throws IOException {
        ProgressLogger logger = new ProgressLogger(true);
        Map<String, Integer> result = new HashMap<>();
        System.out.println("Limit before start: " + getLimit());
        Issues issues = repo.getIssues("?state=closed&per_page=100&labels=" + bugLabel);

        for (Issue issue : issues.getIterable()) {
            if (issue.isBugReport() && issue.isCompleted() && !issue.isPullRequest()){ // Issues can be issues or PRs; here we only want issues.
                for (File file : issue.getChangedFiles(repo)){
                    logger.log("Bug issue " + issue.getNumber() + " changed file " + file.getName());
                    result.merge(file.getName(), 1, Integer::sum);
                }
            }
        }

        System.out.println("Limit at the end: " + getLimit());
        logger.close();
        return result;
    }

    public static int getLimit() throws IOException {
        ObjectRetriever objectRetriever = new ObjectRetriever("https://api.github.com/rate_limit");
        JsonObject jsonObject = objectRetriever.getJsonObject();
        return jsonObject.getJsonObject("resources").getJsonObject("core").getInt("remaining");
    }

}
