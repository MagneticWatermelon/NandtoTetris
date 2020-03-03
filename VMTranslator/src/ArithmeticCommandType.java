
public enum ArithmeticCommandType {
	ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT;	 
    public static ArithmeticCommandType arithmeticCommandType(String arithmeticCommand) {
        switch (arithmeticCommand.toUpperCase()) {
            case "ADD":
                return ADD;
            case "SUB":
                return SUB;
            case "NEG":
                return NEG;
            case "EQ":
                return EQ;
            case "GT":
                return GT;
            case "LT":
                return LT;
            case "AND":
                return AND;
            case "OR":
                return OR;
            case "NOT":
                return NOT;
        }
        return null;
    }
}
