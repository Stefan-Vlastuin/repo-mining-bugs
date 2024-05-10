package repo.mining.bugs;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class ResultWriter {

    final String HEADER = "Filename;Count";
    BufferedWriter writer;

    public ResultWriter(String path) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(path));
        writer.write(HEADER);
        writer.newLine();
    }

    public void write(String fileName, int count) throws IOException {
        writer.write(fileName + ";" + count);
        writer.newLine();
    }

    public void write(Map<String, Integer> bugFiles) throws IOException {
        for (Map.Entry<String, Integer> entry : bugFiles.entrySet()) {
            String key = entry.getKey();
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
