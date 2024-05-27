package repo.mining.bugs.elements;

import repo.mining.bugs.retrievers.ObjectRetriever;

import javax.json.JsonObject;
import java.io.IOException;

public class Commit {
    private final JsonObject jsonObject;
    private Files files;
    private String hash;

    public Commit(String pathJson) throws IOException {
        ObjectRetriever objectRetriever = new ObjectRetriever(pathJson);
        jsonObject = objectRetriever.getJsonObject();
        parseJson();
    }

    public void parseJson() {
        this.files = new Files(jsonObject.getJsonArray("files"));
        this.hash = jsonObject.getString("sha");
    }

    public Files getChangedFiles() {
        return files;
    }

    public String getHash() {return hash;}
}
