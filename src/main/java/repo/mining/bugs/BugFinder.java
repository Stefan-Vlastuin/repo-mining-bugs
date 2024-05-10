package repo.mining.bugs;

import repo.mining.bugs.elements.File;
import repo.mining.bugs.elements.Issue;
import repo.mining.bugs.elements.Issues;
import repo.mining.bugs.elements.Repo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BugFinder {
    private final Repo repo;
    private final ProgressLogger logger;

    public BugFinder(String repoOwner, String repoName, ProgressLogger logger) throws IOException {
        repo = new Repo(repoOwner, repoName);
        this.logger = logger;
    }

    public Map<String, Integer> findBugs(String bugLabel) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        Issues issues = repo.getIssues("?state=closed&per_page=100&labels=" + bugLabel);

        for (Issue issue : issues.getIterable()) {
            if (issue.isBugReport() && issue.isCompleted() && !issue.isPullRequest()){ // Issues can be issues or PRs; here we only want issues.
                for (File file : issue.getChangedFiles(repo)){
                    logger.log("Bug issue " + issue.getNumber() + " changed file " + file.getName());
                    result.merge(file.getName(), 1, Integer::sum);
                }
            }
        }

        return result;
    }

}
