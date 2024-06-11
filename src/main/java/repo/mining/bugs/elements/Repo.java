package repo.mining.bugs.elements;

import repo.mining.bugs.retrievers.ObjectRetriever;

import javax.json.JsonObject;
import java.io.IOException;

public class Repo {

    private final static String baseUrl = "https://api.github.com/repos/";
    private final String owner;
    private final String name;
    private final JsonObject jsonObject;
    private String issues_url;
    private Issues issues;
    private int id;

    public Repo(String owner, String repoName) throws IOException, InterruptedException {
        this.owner = owner;
        this.name = repoName;
        String pathJson = baseUrl + owner + "/" + repoName;
        ObjectRetriever objectRetriever = new ObjectRetriever(pathJson);
        jsonObject = objectRetriever.getJsonObject();
        parseJson();
    }

    private void parseJson(){
        this.issues_url = jsonObject.getString("issues_url").replaceAll("\\{/number}$", "");
        this.id = jsonObject.getInt("id");
    }

    public Issues getIssues(String queryParameters) throws IOException, InterruptedException {
        if (issues == null){
            issues = new Issues(issues_url, queryParameters);
        }
        return issues;
    }

    public int getId(){
        return id;
    }

    public String getOwner(){return owner;}

    public String getName(){return name;}

}
