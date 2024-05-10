package repo.mining.bugs;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connector {
    private HttpURLConnection conn;
    private InputStream is;
    private JsonReader reader;

    public Connector(String path) {
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            String key = System.getenv("GITHUB_API_KEY");
            if (key != null && !key.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + key);
            }
            conn.connect();
            is = conn.getInputStream();
            reader = Json.createReader(is);
        } catch (Exception ignored){

        }
    }

    public JsonArray getJsonArray (){
        if (reader == null){
            return Json.createArrayBuilder().build(); // Create empty array
        }
        return reader.readArray();
    }

    public JsonObject getJsonObject (){
        if (reader == null){
            return Json.createObjectBuilder().build(); // Create empty object
        }
        return reader.readObject();
    }

    public void close (){
        try {
            conn.disconnect();
            is.close();
            reader.close();
        } catch (Exception ignored) {

        }
    }

}
