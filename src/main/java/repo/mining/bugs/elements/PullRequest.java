package repo.mining.bugs.elements;

import repo.mining.bugs.retrievers.ArrayRetriever;
import repo.mining.bugs.retrievers.ObjectRetriever;

import javax.json.JsonObject;
import java.io.IOException;

public class PullRequest {
    private final JsonObject jsonObject;
    private String url;
    private Files files;
    private String state;
    private boolean merged;

    public PullRequest(String pathJson) throws IOException {
        ObjectRetriever objectRetriever = new ObjectRetriever(pathJson);
        jsonObject = objectRetriever.getJsonObject();
        parseJson();
    }

    private void parseJson() {
        this.url = jsonObject.getString("url");
        this.state = jsonObject.getString("state");
        this.merged = jsonObject.getBoolean("merged");
    }

    public Files getChangedFiles() throws IOException {
        if (files == null){
            ArrayRetriever arrayRetriever = new ArrayRetriever(url + "/files");
            files = new Files(arrayRetriever.getJsonArray());
        }
        return files;
    }

    public boolean isClosed(){
        return state.equals("closed");
    }

    public boolean isMerged(){
        return merged;
    }

}
