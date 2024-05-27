package repo.mining.bugs;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class ResultWriter {

    final String HEADER = "FileName;Commit;PullRequest;Count";
    BufferedWriter writer;

    public ResultWriter(String path) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(path));
        writer.write(HEADER);
        writer.newLine();
    }

    public void write(Location location, int count) throws IOException {
        writer.write(location.fileName() + ";" + location.commitHash() + ";" + location.pullRequestNumber() + ";" + count);
        writer.newLine();
    }

    public void write(Map<Location, Integer> bugFiles) throws IOException {
        for (Map.Entry<Location, Integer> entry : bugFiles.entrySet()) {
            Location key = entry.getKey();
            Integer value = entry.getValue();
            write(key, value);
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
