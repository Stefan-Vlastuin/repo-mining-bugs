package repo.mining.bugs.elements;

import repo.mining.bugs.ArrayRetriever;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Issues {
    private final JsonArray jsonArray;
    private final ArrayList<Issue> issues = new ArrayList<>();

    public Issues(String pathJson, String queryParameters) throws IOException {
        ArrayRetriever arrayRetriever = new ArrayRetriever(pathJson + queryParameters);
        jsonArray = arrayRetriever.getJsonArray();
        parseJson();
    }

    private void parseJson(){
        for (JsonValue jsonValue : jsonArray) {
            issues.add(new Issue(jsonValue.asJsonObject()));
        }
    }

    public Iterable<Issue> getIterable(){
        return issues;
    }

}
