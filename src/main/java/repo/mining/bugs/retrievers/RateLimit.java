package repo.mining.bugs.retrievers;

import repo.mining.bugs.ProgressLogger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;

import static java.lang.Thread.sleep;

public class RateLimit {
    private static RateLimit instance;
    private int remainingRequests;
    private final static String LIMIT_URL = "https://api.github.com/rate_limit";
    private final static ProgressLogger logger = ProgressLogger.getInstance();

    private RateLimit() throws IOException {
        remainingRequests = retrieveLimit("remaining");
    }

    public static RateLimit getInstance() throws IOException, InterruptedException {
        if (instance == null){
            instance = new RateLimit();
        }
        return instance;
    }

    public void makeRequest() throws InterruptedException, IOException {
        if (remainingRequests <= 0) {
            int resetTime = retrieveLimit("reset");
            logger.log("Reset time: " + resetTime);
            long timeToWait = calculateTimeToWait(resetTime) + 10; // 10 extra seconds to be sure
            logger.log("Waiting " + timeToWait + " s to get new requests");
            sleep(timeToWait * 1000); // In ms
            remainingRequests = retrieveLimit("remaining");
            logger.log("Remaining requests after waiting: " + remainingRequests);
        }

        remainingRequests--;
    }

    public int retrieveLimit(String field) throws IOException {
        JsonObject jsonObject = retrieveData();
        if (jsonObject != null){
            return jsonObject.getJsonObject("resources").getJsonObject("core").getInt(field);
        }
        return 0;
    }

    private JsonObject retrieveData() throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(LIMIT_URL);
            conn = (HttpURLConnection) url.openConnection();
            String key = System.getenv("GITHUB_API_KEY");
            if (conn != null && key != null && !key.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + key);
                conn.connect();
                try (InputStream is = conn.getInputStream();
                     JsonReader reader = Json.createReader(is)) {
                    return reader.readObject();
                }
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }

    private long calculateTimeToWait(int resetTime) {
        return resetTime - Instant.now().getEpochSecond(); // In seconds
    }

}
