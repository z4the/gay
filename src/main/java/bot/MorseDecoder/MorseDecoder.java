package bot.MorseDecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: hofmannt
 * Date: 5/2/14
 * Time: 9:21 AM
 * This class loads the morse code information, gets the files for input and output from the user,
 * and converts morse code into plain text.
 */

public class MorseDecoder {

   public static String MorseDecoder(String input) throws FileNotFoundException{
   System.out.println("fuck");
   		MorseTree<String> morseTree = loadTree(new File("lib/morsecode.txt"));
		return decode(morseTree, input);
      }



	/**
	 * @param morse file containing morse code
	 * @return MorseTree to be used for decoding
	 * @throws FileNotFoundException
	 */
	private static MorseTree<String> loadTree(File morse) throws FileNotFoundException{
		MorseTree<String> morseTree = new MorseTree<String>();
		Scanner scanner = null;
		try{
			scanner = new Scanner(morse);
			String line;
			while (scanner.hasNextLine()){
				line = scanner.nextLine();
				String symbol = line.split("\t| ")[0];
				String code = line.split("\t| ")[1];
				morseTree.add(symbol, code);
			}
		}
		finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return morseTree;
	}

	/**
	 * Returns a String for a file name, collected through System.in
	 * @param prompt String prompting the input
	 * @param mustExist whether or not the file must exist
	 * @return String file name
	 */
	private static String promptFilename(String prompt, boolean mustExist){
		String filename = null;
		boolean fileExists;
		Scanner in =  new Scanner(System.in);
		do{
			System.out.println(prompt);
			filename = in.next();
			fileExists = (new File(filename)).exists();
			if (!fileExists && mustExist){
				System.out.println("File does not exist. Please try again.");
			}
		} while(!fileExists && mustExist);
		return filename;
	}

	/**
	 * Decodes the morse code
	 * @param morseTree tree containing morse code
	 * @param inputFile location of encoded file
	 * @param outputFile location to save decoded output
	 * @throws FileNotFoundException
	 */
	private static String decode(MorseTree<String> morseTree, String input){
			ArrayList<String> decodedLines = new ArrayList<String>();
			String[] values = input.split(" ");
			System.out.println(Arrays.asList(values));
				String decodedLine = "";
				for (int i = 0; i<values.length; i++){
					//System.out.println(encodedCharacter);
					if (values[i].equals("|")){
						decodedLine = decodedLine + " ";
					}
					else if (values[i].length() > 0){
						String decodedCharacter = morseTree.decode(values[i]);
						if (decodedCharacter == null){
							System.out.println("Warning: Skipping character "+values[i]);
						}
						else{
							decodedLine += decodedCharacter;
						}
					}
				}
				decodedLines.add(decodedLine);
			return output(decodedLines);
	}

	/**
	 * Writes morse code to the output file
	 * @param encodedLines ArrayList of lines to be written
	 * @param outputFile location of file to be written to
	 * @throws FileNotFoundException
	 */
	private static String output(ArrayList<String> encodedLines) {
		String output = "";
			for (String encodedLine : encodedLines){
				output = output + encodedLine;
			}
		return output;

	}

}