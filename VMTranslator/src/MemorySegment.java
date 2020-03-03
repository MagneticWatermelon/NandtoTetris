
public enum MemorySegment {
	LOCAL,ARGUMENT,THIS,THAT,CONSTANT,STATIC,TEMP,POINTER;
	public static MemorySegment memSegment(String segment) {

		switch(segment.toUpperCase()) {
			case "LOCAL":
				return LOCAL;
			case "ARGUMENT":
				return ARGUMENT;
			case "THIS":
			case "THİS":
				return THIS;
			case "THAT":
				return THAT;
			case "CONSTANT":
				return CONSTANT;
			case "STATIC":
				return STATIC;
			case "TEMP":
				return TEMP;
			case "POINTER":
				return POINTER;			
		}
		return null;
	}
}
