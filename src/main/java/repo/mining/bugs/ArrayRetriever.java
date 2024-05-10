package repo.mining.bugs;

import javax.json.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArrayRetriever {

    private final JsonArray jsonArray;

    public ArrayRetriever(String path) throws IOException {
        jsonArray = retrieveAllData(path);
    }

    private JsonArray retrieveAllData(String path) throws IOException {
        JsonArrayBuilder result = Json.createArrayBuilder();
        String key = System.getenv("GITHUB_API_KEY");
        String nextPage = path;
        while (nextPage != null){
            HttpURLConnection conn = null;
            try {
                URL url = new URL(nextPage);
                conn = (HttpURLConnection) url.openConnection();
                if (conn != null && key != null && !key.isEmpty()){
                    conn.setRequestProperty("Authorization", "Bearer " + key);
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        result.addAll(retrieveData(conn));
                    }
                    nextPage = getNextPage(conn);
                }
            } finally {
                if (conn != null){
                    conn.disconnect();
                }
            }
        }
        return result.build();
    }

    private JsonArrayBuilder retrieveData(HttpURLConnection conn) throws IOException {
        JsonArrayBuilder result = Json.createArrayBuilder();
        try (InputStream is = conn.getInputStream();
             JsonReader reader = Json.createReader(is)) {
            JsonArray jsonArray = reader.readArray();
            for (JsonValue value : jsonArray){
                result.add(value);
            }
        }
        return result;
    }

    private String getNextPage(HttpURLConnection conn) {
        String linkHeader = conn.getHeaderField("Link");
        if (linkHeader != null && linkHeader.contains("rel=\"next\"")) {
            String[] links = linkHeader.split(",");
            for (String link : links) {
                String[] segments = link.split(";");
                if (segments.length > 1 && segments[1].contains("rel=\"next\"")) {
                    String nextPageURL = segments[0].trim();
                    return nextPageURL.substring(1, nextPageURL.length() - 1); // Remove enclosing <>
                }
            }
        }
        return null; // No next page URL found
    }

    public JsonArray getJsonArray (){
        return jsonArray;
    }

}

