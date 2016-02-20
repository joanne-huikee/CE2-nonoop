import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author joanne
 * 
 *         CS2103 CE1 
 *         This class is used for editing a given parameter(textfile) based on user command. 
 *         The commands available for TextBuddy are add, delete, display and clear. 
 *         add <string>: appends a new string(all the words following the add command)to the text file 
 *         display: displays all lines, with their respective line number (following
 *         chronological order, the order in which they are keyed in), present in the file. 
 *         delete <int>: delete the line in the file which corresponds to the line number specified by user 
 *         clear: empty the file
 * 
 *         Assumptions: 
 *         1) All other commands are regarded as invalid command and will not be perform when specified by user. 
 *         2) An invalid and re-enter command prompt will be given when an invalid command was entered. 
 *         3) When an unavailable line number was entered for deletion, an error prompt will be given.
 *         4) For clear and display command, as long as the command keyword is entered correctly (the first word
 *          is clear/display), the command will be performed regardless of what data were behind it.
 *          eg. clear bbchfkag will still result in the file being cleared. 
 *         5)File is saved to disk after each user operation to prevent loss of data from unintended termination 
 *         since no "are u sure?" prompt will be given to double confirm the user operation that was meant to be carried out.
 */

public class TextBuddy {

	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use";
	private static final String MESSAGE_INVALID_COMMAND = "This is an invalid command. Pls re-enter command.";
	private static final String MESSAGE_ADD = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_EMPTY_FILE = "%1$s is empty. Nothing to be displayed";
	private static final String MESSAGE_ERROR = "Error occurred while processing file.";
	private static final String MESSAGE_PRINT = "%1$s. %2$s";
	private static final String MESSAGE_EMPTY_DELETION = "Invalid command. %1$s is empty. Nothing to be deleted.";
	private static final String MESSAGE_DELETE = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_INVALID_DELETE = "Invalid command. There is no line %1$s for deletion";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String MESSAGE_SORT = "%1$s has been sorted alphabetically";
	private static final String COMMAND_ADD = "add";
	private static final String COMMAND_DISPLAY = "display";
	private static final String COMMAND_DELETE = "delete";
	private static final String COMMAND_CLEAR = "clear";
	private static final String COMMAND_SORT = "sort";
	private static final String COMMAND_SEARCH = "search";

	private static final String COMMAND_EXIT = "exit";

	public static void main(String args[]) {
		String fileName = args[0];
		File userFile = openFile(fileName);
		showSystemGreeting(MESSAGE_WELCOME, fileName);
		processCommand(userFile);
	}

	/**
	 * this method open a existing file for editing or creates one if has yet
	 * file to exist
	 * 
	 * @param fileName
	 *            the name of the textfile for editing
	 * @return created file with name as specified
	 */
	private static File openFile(String fileName) {
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * This method read and process the command entered by user. It extracts the
	 * command keyword from entire line.
	 */

	private static void processCommand(File userFile) {
		Scanner sc = new Scanner(System.in);
		String commandLine, commandKey;

		do {
			System.out.print("command: ");
			commandLine = sc.nextLine();
			commandKey = getActionWord(commandLine);
			executeCommand(commandKey, commandLine, userFile);
		} while (!commandKey.equals("exit"));
		sc.close();
	}

	private static void showSystemGreeting(String message, String fileName) {
		System.out.println(String.format(message, fileName));
	}

	/**
	 * This method splits and extracts the first word from an entire line.
	 * 
	 * @param commandLine
	 * @return the first word of the whole line keyed in by user
	 */
	public static String getActionWord(String commandLine) {
		String[] tokens = commandLine.split("\\s");
		StringBuilder sb = new StringBuilder();
		sb.insert(0, tokens[0]);
		return sb.toString();
	}

	/**
	 * This method checks for the validity of the command entered by user by
	 * matching it with the system defined ones and prompt for re-enter when
	 * command is invalid.
	 */
	private static void executeCommand(String commandKey, String commandLine, File userFile) {
		switch (commandKey) {
		case COMMAND_ADD:
			add(commandLine, userFile);
			break;
		case COMMAND_DISPLAY:
			display(userFile);
			break;
		case COMMAND_DELETE:
			delete(commandLine, userFile);
			break;
		case COMMAND_CLEAR:
			clear(userFile);
			break;
		case COMMAND_SORT:
			sortAlpha(userFile);
			break;
		case COMMAND_SEARCH:
			search(commandLine, userFile);
			break;
		case COMMAND_EXIT:
			exit();
		default:
			showFeedbackMsg(MESSAGE_INVALID_COMMAND);
		}
		return;
	}

	private static void showFeedbackMsg(String message) {
		System.out.println(message);
	}

	private static void add(String commandLine, File userFile) {
		String message = getMessage(commandLine);
		writeToFile(userFile, message);
		showFeedbackMsg(String.format(MESSAGE_ADD, userFile.getName(), message));
	}

	/**
	 * This method appends a string to the user's file.
	 */
	private static void writeToFile(File userFile, String message) {
		try {
			FileWriter fw = new FileWriter(userFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(message).append("\n").toString();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method get the information in which user wants to store into file by
	 * removing the command word and white spaces at head and tail.
	 */
	public static String getMessage(String commandLine) {
		String message = commandLine.replace(getActionWord(commandLine), " ");
		return message.trim();
	}

	private static void display(File userFile) {
		String line = null;
		int lineNum = 0;
		if (isEmpty(userFile)) {
			showFeedbackMsg(String.format(MESSAGE_EMPTY_FILE, userFile.getName()));
		} else {
			readAndOutputFile(userFile, line, lineNum);
		}
	}

	/**
	 * This method reads the content in the file and output the content line by
	 * line to user with the indication of line numbers.
	 */
	private static void readAndOutputFile(File userFile, String line, int lineNum) {
		try {
			FileReader fr = new FileReader(userFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				lineNum++;
				showFeedbackMsg(String.format(MESSAGE_PRINT, lineNum, line));
			}
			br.close();
		} catch (FileNotFoundException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		} catch (IOException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
	}

	public static boolean isEmpty(File userFile) {
		return userFile.length() <= 0;
	}

	private static void delete(String commandLine, File userFile) {
		Vector<String> temp = new Vector<String>();
		extractLineForDelete(temp, commandLine, userFile);
		emptyFile(userFile);
		appendBackNonDeleted(temp, userFile);
	}

	private static void extractLineForDelete(Vector<String> temp, String commandLine, File userFile) {
		int x = getDeletedLineNum(commandLine);
		if (isEmpty(userFile)) {
			showFeedbackMsg(String.format(MESSAGE_EMPTY_DELETION, userFile.getName()));
		} else {
			searchAndStore(temp, userFile, x);
		}
	}

	/**
	 * This method extracts the integer line number from the string of command
	 */
	public static int getDeletedLineNum(String commandLine) {
		try {
			return Integer.parseInt(commandLine.replaceAll("\\D+", ""));
		} catch (NumberFormatException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
		return -1;
	}

	/**
	 * This method search for the line that needs to be deleted and stores all
	 * other lines into a temporary vector
	 */
	private static void searchAndStore(Vector<String> temp, File userFile, int x) {
		String line = null;
		int lineNum = 1;
		try {
			FileReader fr = new FileReader(userFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				if (lineNum == x) {
					showFeedbackMsg(String.format(MESSAGE_DELETE, userFile.getName(), line));
				} else {
					storeToTemp(temp, line);
				}
				lineNum++;
			}
			br.close();
			if (x >= (lineNum) || x == 0) {
				showFeedbackMsg(String.format(MESSAGE_INVALID_DELETE, x));
			}
		} catch (FileNotFoundException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		} catch (IOException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
	}

	private static void storeToTemp(Vector<String> temp, String line) {
		temp.add(line);
	}

	/**
	 * This method appends all the lines stored in string vector back into the
	 * emptied file from the start to the end of vector.
	 */
	private static void appendBackNonDeleted(Vector<String> temp, File userFile) {
		Iterator<String> i = temp.iterator();
		try {
			FileWriter fw = new FileWriter(userFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			while (i.hasNext()) {
				bw.append(i.next()).append("\n").toString();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void clear(File userFile) {
		emptyFile(userFile);
		showFeedbackMsg(String.format(MESSAGE_CLEAR, userFile.getName()));
	}

	private static void emptyFile(File userFile) {
		try {
			FileWriter fw = new FileWriter(userFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			showFeedbackMsg(MESSAGE_ERROR);
			e.printStackTrace();
		}
	}
	
	private static void sortAlpha(File userFile) {
		String line = null;
		Vector<String> temp = new Vector<String>();
		try {
			FileReader fr = new FileReader(userFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				storeToTemp(temp, line);
			}
			br.close();
		} catch (FileNotFoundException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		} catch (IOException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
		Collections.sort(temp);
		emptyFile(userFile);
		appendBackNonDeleted(temp, userFile);
		showFeedbackMsg(String.format(MESSAGE_SORT, userFile.getName()));
	}

	private static void search(String commandLine, File userFile) {
		Vector<String> temp = new Vector<String>();
		String searchInput = getWordForSearch(commandLine);
		findAndStore(searchInput, temp, userFile);
		printSearchedLine(temp);
	}

	private static String getWordForSearch(String commandLine) {
		String searchInput = commandLine.replace(getActionWord(commandLine), " ");
		return searchInput.trim();
	}

	private static void findAndStore(String searchInput, Vector<String> temp, File userFile) {
		String line = null;
		int lineNum = 0;
		try {
			FileReader fr = new FileReader(userFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				lineNum++;
				if (line.contains(searchInput)) {
					storeToTemp(temp,lineNum +". " + line);
				}
			}
			br.close();
		} catch (FileNotFoundException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		} catch (IOException ex) {
			showFeedbackMsg(MESSAGE_ERROR);
		}
	}

	private static void printSearchedLine(Vector<String> temp) {
		Iterator<String> i = temp.iterator();
		if (temp.isEmpty()) {
			System.out.println("No match found for search");
		} else {
			while (i.hasNext()) {
				System.out.println(i.next().toString());
			}
		}
	}	

	private static void exit() {
		System.exit(0);
	}
}