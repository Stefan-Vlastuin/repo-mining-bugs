package repo.mining.bugs;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ObjectRetriever {

    private final JsonObject jsonObject;
    private HttpURLConnection conn;

    public ObjectRetriever(String path) throws IOException {
        jsonObject = retrieveData(path);
    }

    private JsonObject retrieveData(String path) throws IOException {
        try {
            URL url = new URL(path);
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

    public JsonObject getJsonObject (){
        return jsonObject;
    }

}
