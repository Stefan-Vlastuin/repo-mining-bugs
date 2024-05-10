package repo.mining.bugs.elements;

import repo.mining.bugs.ArrayRetriever;
import repo.mining.bugs.ObjectRetriever;

import javax.json.JsonObject;
import java.io.IOException;

public class Commit {
    private final JsonObject jsonObject;
    private String hash;
    private Files files;

    public Commit(String pathJson) throws IOException {
        ObjectRetriever objectRetriever = new ObjectRetriever(pathJson);
        jsonObject = objectRetriever.getJsonObject();
        parseJson();
    }

    public void parseJson() {
        this.hash = jsonObject.getString("sha");
        this.files = new Files(jsonObject.getJsonArray("files"));
    }

    public Files getChangedFiles() {
        return files;
    }
}
