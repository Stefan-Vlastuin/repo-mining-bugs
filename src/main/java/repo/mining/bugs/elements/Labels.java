package repo.mining.bugs.elements;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.ArrayList;

public class Labels {
    private final ArrayList<Label> labels = new ArrayList<>();

    public Labels (JsonArray jsonArray){
        for (JsonValue jsonValue : jsonArray) {
            this.labels.add(new Label(jsonValue.asJsonObject()));
        }
    }

    // If any of the labels contains a substring.
    public boolean containsSubstring(String substring){
        return labels.stream().anyMatch(l -> l.getName().toLowerCase().contains(substring));
    }
}
