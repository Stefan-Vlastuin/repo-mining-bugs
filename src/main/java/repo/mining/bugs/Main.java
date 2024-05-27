package repo.mining.bugs;

import repo.mining.bugs.retrievers.ObjectRetriever;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.Map;

public class Main {

    // TODO: maybe we need to continuously write output instead of buffering, in case a problem occurs (e.g. rate limits)
    // TODO: which path do we need to store? now we do full path, Zuilhof did filename + parent directory
    // TODO: option to only include files from certain directories? (to filter out test files or non-Java files)

    private final static String RESULT_PATH = "output/output.csv";
    private final static ProgressLogger LOGGER = new ProgressLogger(true);

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Main <repo-user> <repo-name> <bug-label>");
            System.exit(1);
        }
        String repoUser = args[0];
        String repoName = args[1];
        String bugLabel = args[2]; // Needs to be exact match!

        LOGGER.log("Start with " + getRemainingLimit() + " remaining requests");

        Map<Location, Integer> bugFiles = null;
        try {
            BugFinder bugFinder = new BugFinder(repoUser, repoName, LOGGER);
            bugFiles = bugFinder.findBugs(bugLabel);
        } catch (IOException e) {
            LOGGER.log(e);
        } finally {
            try {
                if (bugFiles != null) {
                    writeResults(bugFiles);
                }
            } catch (IOException e) {
                LOGGER.log(e);
            }
            LOGGER.log("End with " + getRemainingLimit() + " remaining requests");
            LOGGER.close();
        }
    }

    public static void writeResults(Map<Location, Integer> bugFiles) throws IOException {
        ResultWriter resultWriter = new ResultWriter(RESULT_PATH);
        resultWriter.write(bugFiles);
        resultWriter.close();
    }

    public static int getRemainingLimit() {
        try {
            ObjectRetriever objectRetriever = new ObjectRetriever("https://api.github.com/rate_limit");
            JsonObject jsonObject = objectRetriever.getJsonObject();
            return jsonObject.getJsonObject("resources").getJsonObject("core").getInt("remaining");
        } catch (IOException e) {
            return -1;
        }
    }

}