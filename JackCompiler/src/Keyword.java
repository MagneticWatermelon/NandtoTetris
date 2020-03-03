
public enum Keyword {
	CLASS("class"),
	CONSTRUCTOR("constructor"),
	FUNCTION("function"),
	METHOD("method"),
	FIELD("field"),
	STATIC("static"),
	VAR("var"),
	INT("int"),
	CHAR("char"),
	BOOLEAN("boolean"),
	VOID("void"),
	TRUE("true"),
	FALSE("false"),
	NULL("null"),
	THIS("this"),
	LET("let"),
	DO("do"),
	IF("if"),
	ELSE("else"),
	WHILE("while"),
	RETURN("return");

	private String keyword;

	Keyword(String kw) {
		this.setKeyword(kw);
	}

	public String getKeyword() {
		return keyword;
	}

	public static String get(String keyword) {
		Keyword[] keywords = Keyword.values();
		for (Keyword value : keywords) {
			if (value.getKeyword().equals(keyword)) {
				return value.getKeyword();
			}
		}
		return null;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public static boolean isKeyword(String token) {
		Keyword[] keywords = Keyword.values();
		for (Keyword value : keywords) {
			if (value.getKeyword().equals(token)) {
				return true;
			}
		}
		return false;
	}
}
