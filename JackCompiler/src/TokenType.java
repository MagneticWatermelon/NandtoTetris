
public enum TokenType {
	KEYWORD ("keyword"),
	SYMBOL ("symbol"),
	IDENTIFIER ("identifier"),
    INTEGER_CONSTANT ("integerConstant"),
    STRING_CONSTANT ("stringConstant");
 
    private final String tokenAsString;
 
    TokenType(String tokenAsString) {
        this.tokenAsString = tokenAsString;
    }

    public String getTokenAsString() {
		return tokenAsString;
	}
}
