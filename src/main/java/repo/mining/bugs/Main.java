package repo.mining.bugs;

import com.jcabi.github.*;
import repo.mining.bugs.elements.File;

import java.io.IOException;
import java.util.Map;

public class Main {

    // TODO: maybe we need to continuously write output instead of buffering, in case a problem occurs (e.g. rate limits)
    // TODO: which path do we need to store? now we do full path, Zuilhof did filename + parent directory
    // TODO: option to only include files from certain directories? (to filter out test files or non-Java files)

//    private final static String REPO_USER = "Stefan-Vlastuin";
//    private final static String REPO_NAME = "TestRepo";
    private final static String REPO_USER = "javaparser";
    private final static String REPO_NAME = "javaparser";
    private final static String RESULT_PATH = "output/output.csv";
    private final static String BUG_LABEL = "Bug+report"; // Needs to be an exact match; adjust per project
    //private final static String BUG_LABEL = "bug"; // Needs to be an exact match; adjust per project

    public static void main(String[] args) {
        try {
            NewController newController = new NewController();
            Map<String, Integer> bugFiles = newController.findBugs(BUG_LABEL);
            ResultWriter resultWriter = new ResultWriter(RESULT_PATH);
            resultWriter.write(bugFiles);
            resultWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String key = System.getenv("GITHUB_API_KEY");
//        Github github;
//        if (key == null || key.isEmpty()) {
//            github = new RtGithub();
//        } else {
//            github = new RtGithub(key);
//        }
//
//        Limit.Smart limit = new Limit.Smart(github.limits().get("core"));
//        try {
//            System.out.println("limits: " + limit.remaining());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Repo repo = github.repos().get(new Coordinates.Simple(REPO_USER, REPO_NAME));
//        Controller controller = new Controller(repo, github);
//        controller.findBugFiles();
//        Map<String, Integer> bugFiles = controller.getBugFiles();
//        controller.close();
//
//        ResultWriter resultWriter = new ResultWriter(RESULT_PATH);
//        resultWriter.write(bugFiles);
//        resultWriter.close();
    }

}