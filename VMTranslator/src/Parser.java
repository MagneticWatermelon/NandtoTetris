import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
	private Scanner fs = null;
	private String currentCommand = null;
	private CommandType currentCommandType;
	private String[] parsedCommand = null;

	public Parser(File infile) {
		try {
			fs=new Scanner(infile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}

	public boolean hasMoreCommands() {
		return fs.hasNext();
	}

	public void advance() {
		currentCommand = fs.nextLine().trim();
		parsedCommand = currentCommand.split(" ");
	}

	public CommandType commandType() {
        String command = parsedCommand[0];
        currentCommandType = CommandType.commandType(command);
        return currentCommandType;
    }

    public String arg1() {
        if (currentCommandType == CommandType.C_ARITHMETIC) {
            return parsedCommand[0];
        } else {
            return parsedCommand[1];
        }
    }

    public String arg2() {
        return parsedCommand[2];
    }
}
