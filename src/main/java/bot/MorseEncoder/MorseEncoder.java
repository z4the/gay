package bot.MorseEncoder;
import java.util.*;
public class MorseEncoder {
    // Morse code by indexing
    private final String[] code
            = { ".-",   "-...", "-.-.", "-..",  ".",
            "..-.", "--.",  "....", "..",   ".---",
            "-.-",  ".-..", "--",   "-.",   "---",
            ".--.", "--.-", ".-.",  "...",  "-",
            "..-",  "...-", ".--",  "-..-", "-.--",
            "--..", "|" };

    public ArrayList<String> englishToMorse(String englishLang) {
        englishLang = englishLang.toLowerCase();
        ArrayList<String> inputChars = new ArrayList<String>();
        for (int i = 0; i < englishLang.length(); i++) {
            inputChars.add(this.code[englishLang.charAt(i) - 'a']);
        }
        //return the arraylist of characters
        return inputChars;
    }

}