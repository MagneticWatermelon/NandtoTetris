import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
	private Token currentToken;
	private int pointer;
	private ArrayList<Token> tokens;

	private static Pattern tokenPatterns;
	private static String keyWordReg;
	private static String symbolReg;
	private static String intReg;
	private static String strReg;
	private static String idReg;

	public Tokenizer(File inFile) {
		try {
			Scanner scanner = new Scanner(inFile);
			String preprocessed = "";
			String line = "";

			while(scanner.hasNext()){
				line = CommentRemover.removeSingleLC(line).trim();
				if (line.length() > 0) {
					preprocessed += line + "\n";
				}
			}

			preprocessed = CommentRemover.removeMultiLC(preprocessed).trim();
			initRegs();

			Matcher m = tokenPatterns.matcher(preprocessed);
			tokens = new ArrayList<>();
			pointer = 0;

			while (m.find()){
				Token t = new Token();
				t.setTokenVal(m.group());
				tokens.add(t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		currentToken = null;
	}

	private void initRegs(){

		keyWordReg = "";
		for (Keyword key: Keyword.values()){
			keyWordReg += key.getKeyword() + "|";
		}
		symbolReg = "[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]";
		intReg = "[0-9]+";
		strReg = "\"[^\"\n]*\"";
		idReg = "[\\w_]+";

		tokenPatterns = Pattern.compile(keyWordReg + symbolReg + "|" + intReg + "|" + strReg + "|" + idReg);
	}

	public boolean hasMoreTokens() {
		return pointer < tokens.size();
	}

	public void advance(){

		if (hasMoreTokens()) {
			currentToken = tokens.get(pointer);
			pointer++;
		}
		else {
			throw new IllegalStateException("No more tokens");
		}

		//System.out.println(currentToken.getTokenVal());
		if (currentToken.getTokenVal().matches(keyWordReg)){
			currentToken.setTokenType(TokenType.KEYWORD);
		}
		else if (currentToken.getTokenVal().matches(symbolReg)){
			currentToken.setTokenType(TokenType.SYMBOL);
		}
		else if (currentToken.getTokenVal().matches(intReg)){
			currentToken.setTokenType(TokenType.INTEGER_CONSTANT);
		}
		else if (currentToken.getTokenVal().matches(strReg)){
			currentToken.setTokenType(TokenType.STRING_CONSTANT);
		}
		else if (currentToken.getTokenVal().matches(idReg)){
			currentToken.setTokenType(TokenType.IDENTIFIER);
		}
		else {

			throw new IllegalArgumentException("Unknown token:" + currentToken);
		}

	}

	public Token getCurrentToken() {
		return currentToken;
	}

	public Token lookAheadToken() {
		if (hasMoreTokens()) {
			return tokens.get(pointer + 1);
		}
		else {
			return null;
		}
	}
}