package repo.mining.bugs.elements;

import repo.mining.bugs.retrievers.ArrayRetriever;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Timeline {
    private final JsonArray jsonArray;
    private final List<Event> events = new ArrayList<>();

    public Timeline(String pathJson) throws IOException {
        ArrayRetriever arrayRetriever = new ArrayRetriever(pathJson);
        jsonArray = arrayRetriever.getJsonArray();
        parseJson();
    }

    private void parseJson() {
        for (JsonValue jsonValue : jsonArray) {
            events.add(new Event(jsonValue.asJsonObject()));
        }
    }

    public List<PullRequest> getLinkedPullRequests(Repo repo) throws IOException {
        List<PullRequest> pullRequests = new ArrayList<>();
        for (Event event : events) {
            if (event.getEvent().equals("cross-referenced") && event.getPullRequest() != null && event.sameRepo(repo)) {
                pullRequests.add(event.getPullRequest());
            }
        }
        return pullRequests;
    }

    public Commit getClosingCommit() throws IOException {
        for (Event event : events) {
            if (event.getEvent().equals("closed")) {
                return event.getCommit(); // Can be null!
            }
        }
        return null;
    }
}
