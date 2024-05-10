package repo.mining.bugs.elements;

import javax.json.JsonObject;

public class Label {
    private final String name;

    public Label(JsonObject jsonObject) {
        this.name = jsonObject.getString("name");
    }

    public String getName() {
        return name;
    }
}
