package repo.mining.bugs.elements;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class Files {
    private final List<File> files = new ArrayList<>();

    public Files (JsonArray jsonArray){
        for (JsonValue jsonValue : jsonArray) {
            this.files.add(new File(jsonValue.asJsonObject()));
        }
    }

    public List<File> getFiles(){
        return files;
    }
}
