package repo.mining.bugs;

public record Location(String fileName, String commitHash, int pullRequestNumber) {

    public Location(String fileName, String commitHash) {
        this(fileName, commitHash, -1);
    }

    public Location(String fileName, int pullRequestNumber) {
        this(fileName, "", pullRequestNumber);
    }

}
