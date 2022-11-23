package stringgenerator;

import java.util.HashSet;
import java.util.Random;

public class StringGenerator {
    private HashSet<String> stringSet = new HashSet<>();

    private int amount;
    private int minLength;
    private int maxLength;
    private char[] chars;

    /** Class for generating strings
     * @param amount number of strings to generate
     * @param minLength minimum number of letters in a string
     * @param maxLength maximum number of letters in a string, >exclusive
     * @param chars characters to make strings from */
    public StringGenerator(int amount, int minLength, int maxLength, char[] chars) {
        this.amount = amount;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.chars = chars;
    }

    public HashSet<String> getStrings() {
        if (!canGenerate()) {
            try {
                throw new IllegalArgumentException("Cannot generate strings with those parameters");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            return stringSet;
        }

        Random randomGenerator = new Random();

        int i = stringSet.size();
        int charSize = chars.length;
        while (i < amount) {
            int length = randomGenerator.nextInt(minLength, maxLength + 1);
            char[] word = new char[length];
            for (int n = 0; n < length; n++) {
                word[n] = chars[randomGenerator.nextInt(charSize)];
            }
            String string = String.copyValueOf(word);
            if (stringSet.contains(string))
                continue;

            stringSet.add(string);
            i++;
        }

        return stringSet;
    }

    public boolean canGenerate() {
        int maxPossibleAmount = 0;
        for (int i = minLength; i <= maxLength; i++) {
            maxPossibleAmount += Math.pow(i, chars.length);
        }

        return (amount <= maxPossibleAmount);
    }
}
