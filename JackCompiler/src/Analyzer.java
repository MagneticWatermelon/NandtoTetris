import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Analyzer {

	public static void main(String[] args) throws FileNotFoundException {
		String fileName = args[0];
		File inFile = new File(fileName);
		File outFile;
		String outfileName = inFile.getName().substring(0, inFile.getName().indexOf('.'));
		outFile = new File(outfileName);
		PrintWriter printWriter = new PrintWriter(outFile);
		CompilationEngine compilationEngine = new CompilationEngine(inFile);
		printWriter.print(compilationEngine.compileClass());
	}
}
