
public enum CommandType {
	C_ARITHMETIC,C_PUSH,C_POP,C_LABEL,C_GOTO,C_IF,C_FUNCTION,C_RETURN,C_CALL;
	public static CommandType commandType(String command) {
		ArithmeticCommandType ac = ArithmeticCommandType.arithmeticCommandType(command);
		if(ac != null) {
			return C_ARITHMETIC;
		}
		switch (command.toUpperCase()) {
	        case "PUSH":
	            return C_PUSH;
	        case "POP":
	            return C_POP;
	        case "LABEL":
	            return C_LABEL;
	        case "GOTO":
	            return C_GOTO;
	        case "IF-GOTO":
	            return C_IF;
	        case "FUNCTION":
	            return C_FUNCTION;
	        case "RETURN":
	            return C_RETURN;
	        case "CALL":
	            return C_CALL;        
		}
		return null;
	}
}
