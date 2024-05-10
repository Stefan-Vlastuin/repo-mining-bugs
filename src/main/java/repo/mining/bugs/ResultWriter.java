package repo.mining.bugs;

import repo.mining.bugs.elements.File;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class ResultWriter {

    final String HEADER = "Filename;Count";
    BufferedWriter writer;

    public ResultWriter(String path){
        try {
            this.writer = new BufferedWriter(new FileWriter(path));
            writer.write(HEADER);
            writer.newLine();
        } catch (IOException ignore) {

        }
    }

    public void write(String fileName, int count){
        try {
            writer.write(fileName + ";" + count);
            writer.newLine();
        } catch (IOException ignore) {

        }
    }

    public void write(File file, int count){
        try {
            writer.write(file.getName() + ";" + count);
            writer.newLine();
        } catch (IOException ignore) {

        }
    }

    public void write(Map<String, Integer> bugFiles){
        bugFiles.forEach(this::write);
    }

//    public void write(Map<File, Integer> bugFiles){
//        bugFiles.forEach(this::write);
//    }

    public void close(){
        try {
            writer.close();
        } catch (IOException ignore) {

        }
    }

}
