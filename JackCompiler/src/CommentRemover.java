
public class CommentRemover {

	public static String removeSingleLC(String line) {
		int commentStartIndex = line.indexOf(("//"));
		if (commentStartIndex != -1) {
			line = line.substring(0, commentStartIndex);
		}
		return line;
	}

	public static String removeMultiLC(String lines) {
		int commentStartIndex = lines.indexOf(("/*"));
		if (commentStartIndex == -1) {
			return lines;
		}
		String resultLines = lines;
		int commentEndIndex = lines.indexOf("*/");
		while (commentStartIndex != -1) {
			if (commentEndIndex == -1) {
				return lines.substring(0, commentStartIndex - 1);
			}
			resultLines = resultLines.substring(0,commentStartIndex) + resultLines.substring(commentEndIndex + 2);
			commentStartIndex = resultLines.indexOf("/*");
			commentEndIndex = resultLines.indexOf("*/");
		}
		return resultLines;
	}

	public static String removeSpaces(String line) {
		StringBuilder sb = new StringBuilder();
		if (line.length() != 0) {
			String[] words = line.split(" ");
			for (String s : words) {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String spaceTest = "Hello world, this is a test for removing white spaces in a string!";
		String singleTest = "This is a test for single line comments // This is a comment";
		String multiTest = "This is a test for multi line comments /* Comment at first line\nContinues at line 2\nline3\nends here*/Rest of the code";

		System.out.println(spaceTest);
		String test = CommentRemover.removeSpaces(spaceTest);
		System.out.println(test);
		System.out.println();

		System.out.println(singleTest);
		test = CommentRemover.removeSingleLC(singleTest);
		System.out.println(test);
		System.out.println();

		System.out.println(multiTest);
		test = CommentRemover.removeMultiLC(multiTest);
		System.out.println(test);
		System.out.println();
	}
}
