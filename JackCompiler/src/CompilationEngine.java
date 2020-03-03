import java.io.File;
import java.io.FileNotFoundException;

public class CompilationEngine {
	public Token currentToken;
	private StringBuilder sb = new StringBuilder();
	private Tokenizer tokenizer;

	CompilationEngine(File inFile) {
        tokenizer = new Tokenizer(inFile);
    }

	private void printCurrentToken() {
		sb.append('<');
		sb.append(currentToken.getTokenType().getTokenAsString());
		sb.append('>');
		sb.append(currentToken.getTokenVal());
		sb.append("</");
		sb.append(currentToken.getTokenType().getTokenAsString());
		sb.append('>');
		sb.append("\n");
    }
	
	private void advanceCurrentToken() {
		tokenizer.advance();
		setCurrentToken();
	}

	private void setCurrentToken() {
		currentToken = tokenizer.getCurrentToken();
	}

    private boolean isSubRoutineDeclarationStart(final String token) {
    	String[] list = {"constructor", "function", "method" };
        for (String s : list) {
            if (s.equals(token)) {
                return true;
            }
        }
    	return false;
    }
 
    private boolean isStatementStart(final String token) {
    	String[] list = {"let", "if", "while", "do", "return" };
        for (String s : list) {
            if (s.equals(token)) {
                return true;
            }
        }
    	return false;
    }
 
 
    private boolean isOp(String token) {
    	String[] list = {"+", "-", "*", "/", "&", "|", "<", ">", "=", "&lt;", "%gt;", "&amp;" };
        for (String s : list) {
            if (s.equals(token)) {
                return true;
            }
        }
    	return false;
    }
 
    private boolean isUnaryOp(String token) {
    	String[] list = {"-", "~"};
        for (String s : list) {
            if (s.equals(token)) {
                return true;
            }
        }
    	return false;
    }

    private void printAndAdvanceToken() {
	    printCurrentToken();
	    advanceCurrentToken();
    }

    public String compileClass() {
        currentToken = tokenizer.getCurrentToken();
        sb.append("<class>");
        sb.append("\n");

        printAndAdvanceToken(); // <keyword> class </keyword>
        printAndAdvanceToken(); // <identifier> main </identifier>
        printAndAdvanceToken(); // <symbol> { </symbol>
        compileClassVarDeclarations();
        compileSubRoutineDeclarations();
        printCurrentToken(); // <symbol> } </symbol>

        sb.append("</class>");
        sb.append("\n");
        return sb.toString();
    }
 
    private void compileClassVarDeclarations() {
        while (!isSubRoutineDeclarationStart(currentToken.getTokenVal())) {
            if (currentToken.getTokenVal().equals("}")) {
                break;
            }
            compileClassVarDeclaration();
        }
    }
 
    private void compileClassVarDeclaration() {
    	sb.append("<classVarDec>");
    	sb.append("\n");
 
        while (!currentToken.getTokenVal().equals(";")) {
            printAndAdvanceToken();
        }
        printAndAdvanceToken();
 
        sb.append("</classVarDec>");
        sb.append("\n");
    }
 
    private void compileSubRoutineDeclarations() {
        while (isSubRoutineDeclarationStart(currentToken.getTokenVal())) {
            compileSubRoutineDeclaration();
        }
    }
 
    private void compileSubRoutineDeclaration() {
    	sb.append("<subroutineDec>");
    	sb.append("\n");
 
    	printAndAdvanceToken(); // <keyword> function | method | constructor </keyword>
        printAndAdvanceToken(); // (<keyword> void | int | char | boolean </keyword>) | (<identifier> Foo </identifier>)
        printAndAdvanceToken(); // <identifier> main </identifier>
        printAndAdvanceToken(); // <symbol> ( </symbol>
        compileParameterList();
        printAndAdvanceToken(); // <symbol> ) </symbol>
        compileSubRoutineBody();

        sb.append("</subroutineDec>");
        sb.append("\n");
    }
 
    private void compileParameterList() {
    	sb.append("<parameterList>");
        sb.append("\n");
 
        while (!currentToken.getTokenVal().equals(")")) {
            printAndAdvanceToken();
        }

        sb.append("</parameterList>");
        sb.append("\n");
    }
 
    private void compileSubRoutineBody() {
    	sb.append("<subroutineBody>");
    	sb.append("\n");

        printAndAdvanceToken(); // <symbol> { </symbol>
        compileVarDecs();
        compileStatements();
        printAndAdvanceToken(); // <symbol> } </symbol>
 
        sb.append("</subroutineBody>");
        sb.append("\n");
    }
 
    private void compileVarDecs() {
        while (currentToken.getTokenVal().equals("var")) {
        	sb.append("<varDec>");
        	sb.append("\n");
 
        	printAndAdvanceToken(); // <keyword> var </keyword>
            printAndAdvanceToken(); // <keyword> int </keyword>
            printAndAdvanceToken(); // <identifier> foo </identifier>
            while (currentToken.getTokenVal().equals(",")) {
                printAndAdvanceToken(); // <identifier> , </identifier>
                printAndAdvanceToken(); // <identifier> bar </identifier>
            }
            printAndAdvanceToken(); // <symbol> ; </symbol>
 
            sb.append("</varDec>");
            sb.append("\n");
        }
    }
 
    private void compileStatements() {
    	sb.append("<statements>");
    	sb.append("\n");
 
        while (isStatementStart(currentToken.getTokenVal())) {
            final String tokenValue = currentToken.getTokenVal();
            if (tokenValue.equals("let")) {
                compileLetStatement();
            }
            if (tokenValue.equals("return")) {
                compileReturnStatement();
            }
            if (tokenValue.equals("if")) {
                compileIfStatement();
            }
            if (tokenValue.equals("while")) {
                compileWhileStatement();
            }
            if (tokenValue.equals("do")) {
            	sb.append("<doStatement>");
            	sb.append("\n");

                printAndAdvanceToken();
                compileSubRoutineCall();
                printAndAdvanceToken();
 
                sb.append("</doStatement>");
                sb.append("\n");
            }
        }
 
        sb.append("</statements>");
        sb.append("\n");
    }
 
    private void compileWhileStatement() {
    	sb.append("<whileStatement>");
    	sb.append("\n");

        printAndAdvanceToken(); // <keyword> while </keyword>
        printAndAdvanceToken(); // <symbol> ( </symbol>
        compileSingleExpression();
        printAndAdvanceToken(); // <symbol> ) </symbol>
        printAndAdvanceToken(); // <symbol> { </symbol>
        compileStatements();
        printAndAdvanceToken(); // <symbol> } </symbol>
 
        sb.append("</whileStatement>");
        sb.append("\n");
    }
 
    private void compileIfStatement() {
    	sb.append("<ifStatement>");
    	sb.append("\n");

        printAndAdvanceToken(); // <keyword> if </keyword>
        printAndAdvanceToken(); // <symbol> ( </symbol>
        compileSingleExpression();
        printAndAdvanceToken(); // <symbol> ) </symbol>
        printAndAdvanceToken(); // <symbol> { </symbol>
        compileStatements();
        printAndAdvanceToken(); // <symbol> } </symbol>
        if (currentToken.getTokenVal().equals("else")) {
            printAndAdvanceToken(); // <identifier> else </identifier>
            printAndAdvanceToken(); // <symbol> { </symbol>
            compileStatements();
            printAndAdvanceToken(); // <symbol> } </symbol>
        }
 
        sb.append("</ifStatement>");
        sb.append("\n");
    }
 
    private void compileLetStatement() {
    	sb.append("<letStatement>");
    	sb.append("\n");

        printAndAdvanceToken(); // <keyword> let </keyword>
        printAndAdvanceToken(); // <identifier> a </identifier>
        if (currentToken.getTokenVal().equals("[")) {
            printAndAdvanceToken(); // <symbol> [ </symbol>
            compileSingleExpression();
            printAndAdvanceToken(); // <symbol> ] </symbol>
        }
        printAndAdvanceToken(); // <symbol> = </symbol>
        compileSingleExpression();
        printAndAdvanceToken(); // <symbol> ; </symbol>
 
        sb.append("</letStatement>");
        sb.append("\n");
    }
 
    private void compileReturnStatement() {
    	sb.append("<returnStatement>");
    	sb.append("\n");

        printAndAdvanceToken();
        if (!currentToken.getTokenVal().equals(";")) {
            compileSingleExpression();
        }
        printAndAdvanceToken(); // <symbol> ; </symbol>
 
        sb.append("</returnStatement>");
        sb.append("\n");
    }
 
    private void compileSingleExpression() {
    	sb.append("<expression>");
    	sb.append("\n");
 
        compileTerm();
        while (isOp(currentToken.getTokenVal())) {
            printAndAdvanceToken();
            compileTerm();
        }
 
        sb.append("</expression>");
        sb.append("\n");
    }
 
    private void compileTerm() {
    	sb.append("<term>");
    	sb.append("\n");
 
        if (currentToken.getTokenType() == TokenType.INTEGER_CONSTANT) {
            compileIntegerConstant();
        }
        else if (currentToken.getTokenType() == TokenType.STRING_CONSTANT) {
            compileStringConstant();
        }
        else if (currentToken.getTokenType() == TokenType.KEYWORD) {
            compileKeywordConstant();
        }
        else if (currentToken.getTokenVal().equals("(")) {
            printAndAdvanceToken(); // <symbol> ( </symbol>
            compileSingleExpression();
            printAndAdvanceToken(); // <symbol> ) </symbol>
        }
        else if (isUnaryOp(currentToken.getTokenVal())) {
            printAndAdvanceToken(); // <symbol> ~ </symbol>
            compileTerm();
        }
        else if (tokenizer.lookAheadToken().getTokenVal().equals("(") || tokenizer.lookAheadToken().getTokenVal().equals(".")) {
            compileSubRoutineCall();
        }
        else {
            compileIdentifier();
            if (tokenizer.getCurrentToken().getTokenVal().equals("[")) {
                printAndAdvanceToken(); // <symbol> [ </symbol>
                compileSingleExpression();
                printAndAdvanceToken(); // <symbol> ] </symbol>
            }
        }
 
        sb.append("</term>");
        sb.append("\n");
    }
 
    private void compileIdentifier() {
        printAndAdvanceToken(); // <identifier> foo </identifier>
    }
 
    private void compileSubRoutineCall() {
        printAndAdvanceToken(); // <identifier> Keyboard </identifier>
        if (currentToken.getTokenVal().equals("(")) {
            printAndAdvanceToken(); // <symbol> ( </symbol>
            compileExpressionList();
            printAndAdvanceToken(); // <symbol> ) </symbol>
        }
        else {
            printAndAdvanceToken(); // <symbol> . </symbol>
            printAndAdvanceToken(); // subRoutineName..
            printAndAdvanceToken(); // <symbol> ( </symbol>
            compileExpressionList();
            printAndAdvanceToken(); // <symbol> ) </symbol>
        }
    }
 
    private void compileExpressionList() {
    	sb.append("<expressionList>");
    	sb.append("\n");
 
        if (!currentToken.getTokenVal().equals(")")) {
            compileSingleExpression();
            while (currentToken.getTokenVal().equals(",")) {
                printAndAdvanceToken();
                compileSingleExpression();
            }
        }
 
        sb.append("</expressionList>");
        sb.append("\n");
    }
 
    private void compileKeywordConstant() {
        printAndAdvanceToken(); // <keyword> true </keyword>
    }
 
    private void compileIntegerConstant() {
        printAndAdvanceToken(); // <integerConstant> 20 </integerConstant>
    }
 
    private void compileStringConstant() {
        printAndAdvanceToken(); // <stringConstant> hello </string
    }
}