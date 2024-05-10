package repo.mining.bugs;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ProgressLogger {

    private final static String LOG_PATH = "logs/findBugs.log";
    private final Logger logger = Logger.getLogger("repo.mining.bugs");
    FileHandler fh;
    boolean writeToFile;

    public ProgressLogger() {
        this(false);
    }

    public ProgressLogger(boolean writeToFile) {
        this.writeToFile = writeToFile;

        if (writeToFile) {
            try {
                fh = new FileHandler(LOG_PATH, true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            } catch (IOException e) {
                this.writeToFile = false;
            }
        }
    }

    public void log(Level level, String message) {
        this.logger.log(level, message);
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

    public void log(Throwable throwable){
        logger.log(Level.SEVERE, throwable.getMessage(), throwable);
    }

    public void close(){
        if (writeToFile && fh != null) {
            fh.close();
        }
    }

}
