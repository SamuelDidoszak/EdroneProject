package stringgenerator;

import org.springframework.data.util.Pair;
import stringgenerator.Database.DatabaseHandler;

import java.io.File;
import java.util.ArrayDeque;

public class StringThreads {
    private int threadAmount = 0;
    private final ArrayDeque<Pair<String, File>> generatedFilesList = new ArrayDeque<>();

    /**
     * @return amount of threads running currently
     */
    public int getThreadAmount() {
        return threadAmount;
    }

    /** Returns the file array */
    public ArrayDeque<Pair<String, File>> getFiles() {
        return generatedFilesList;
    }

    /** Starts a thread which generates a file with provided parameters or fetches it from the database
     * Generated files can be retrieved via getFiles() method */
    public void startThread(int amount, int minLength, int maxLength, String chars, DatabaseHandler databaseHandler) throws Exception {
        startThread(amount, minLength, maxLength, chars.toCharArray(), databaseHandler);
    }

    /** Starts a thread which generates a file with provided parameters or fetches it from the database
     * Generated files can be retrieved via getFiles() method */
    public void startThread(int amount, int minLength, int maxLength, char[] chars, DatabaseHandler databaseHandler) throws Exception {
        if (!canGenerate(amount, minLength, maxLength, chars))
            throw new Exception("Cannot generate strings with these parameters");

        threadAmount++;
        new Thread(
            () -> {
                StringFetcher stringFetcher = new StringFetcher(amount, minLength, maxLength, chars, databaseHandler);
                try {
                    File generatedFile = stringFetcher.fetchStringFile();
                    generatedFilesList.add(Pair.of(generatedFile.getName(), generatedFile));
                    threadAmount--;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        ).start();
    }

    /**
     * @return true if provided params allow for file generation
     * */
    private boolean canGenerate(int amount, int minLength, int maxLength, char[] chars) {
        int maxPossibleAmount = 0;
        for (int i = minLength; i <= maxLength; i++) {
            maxPossibleAmount += Math.pow(i, chars.length);
        }

        return (amount <= maxPossibleAmount);
    }
}
