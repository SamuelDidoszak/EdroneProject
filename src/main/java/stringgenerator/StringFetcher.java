package stringgenerator;

import stringgenerator.Database.DatabaseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class StringFetcher {
    private DatabaseHandler databaseHandler;
    private int amount;
    /** minimum length of generated strings. Starts from 1, 0 generates no strings */
    private int minLength;
    /** maximum length of generated strings, inclusive */
    private int maxLength;
    private char[] chars;

    /**
     * @param amount number of strings to generate
     * @param minLength minimum length of generated strings. Starts from 1, 0 generates no strings
     * @param maxLength maximum length of generated strings. Inclusive
     * @param chars characters to generate strings from
     */
    public StringFetcher(int amount, int minLength, int maxLength, char[] chars, DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
        this.amount = amount;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.chars = chars;
    }

    /**
     * Fetches a file with provided parameters.
     * Either returns an existing file from the database or generates and adds it there
     * @return file with unique strings, each in a new line
     * @throws Exception
     */
    public File fetchStringFile() throws Exception {
        StringGenerator generator = new StringGenerator(amount, minLength, maxLength, chars);
        if (!generator.canGenerate())
            throw new Exception("Cannot generate strings with these parameters");

        File dbFile = databaseHandler.getFileByParams(generateFileParams());
        if (dbFile != null)
            return dbFile;

        HashSet<String> stringSet = new HashSet<>();
        stringSet = generator.getStrings();

        File file = generateStringFile(stringSet);
        databaseHandler.addFile(generateFileParams(), file);
        file.deleteOnExit();

        return file;
    }

    /**
     * Generates a file with provided parameters
     * If a file already exists, it gets replaced with a new one
     * @return file with unique strings, each in a new line
     * @throws IOException
     */
    private File generateStringFile(HashSet<String> stringSet) throws IOException {
        File file = new File(generateFileParams());

        if (!file.createNewFile()) {
            file.delete();
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);

        for (String string : stringSet) {
            fileWriter.write(string + "\n");
        }
        fileWriter.close();

        return file;
    }

    /** Generates a string from parameters, which is used as an unique identifier for the generated file */
    private String generateFileParams() {
        return new String(chars) + "_" + amount + "_" + minLength + "_" + maxLength + ".txt";
    }
}
