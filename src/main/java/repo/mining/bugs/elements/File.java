package repo.mining.bugs.elements;

import javax.json.JsonObject;

public class File {
    private final JsonObject jsonObject;
    private String filename;

    public File (JsonObject jsonObject){
        this.jsonObject = jsonObject;
        parseJson();
    }

    private void parseJson(){
        this.filename = jsonObject.getString("filename");
    }

    public String getName(){
        return filename;
    }

    @Override
    public String toString(){
        return filename;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof File f){
            return filename.equals(f.filename);
        }
        return false;
    }
}
