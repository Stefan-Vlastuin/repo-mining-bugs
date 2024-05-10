package repo.mining.bugs;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.Map;

public class Main {

    // TODO: maybe we need to continuously write output instead of buffering, in case a problem occurs (e.g. rate limits)
    // TODO: which path do we need to store? now we do full path, Zuilhof did filename + parent directory
    // TODO: option to only include files from certain directories? (to filter out test files or non-Java files)

    private final static String REPO_USER = "javaparser";
    private final static String REPO_NAME = "javaparser";
    private final static String BUG_LABEL = "Bug+report"; // Needs to be an exact match; adjust per project

    private final static String RESULT_PATH = "output/output.csv";

    public static void main(String[] args) {
        ResultWriter resultWriter = null;
        try {
            BugFinder bugFinder = new BugFinder(REPO_USER, REPO_NAME);
            Map<String, Integer> bugFiles = bugFinder.findBugs(BUG_LABEL);

            resultWriter = new ResultWriter(RESULT_PATH);
            resultWriter.write(bugFiles);
            resultWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resultWriter != null) {
                resultWriter.close();
            }
        }
    }

    public static int getLimit() throws IOException {
        ObjectRetriever objectRetriever = new ObjectRetriever("https://api.github.com/rate_limit");
        JsonObject jsonObject = objectRetriever.getJsonObject();
        return jsonObject.getJsonObject("resources").getJsonObject("core").getInt("remaining");
    }

}