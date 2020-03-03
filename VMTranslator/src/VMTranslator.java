import java.io.File;
import java.io.IOException;
public class VMTranslator {

	public static void main(String[] args) throws IOException {
		String source = args[0];
        if (source.endsWith(".vm")) {
            source = source.substring(0, source.lastIndexOf('.'));
        }
        File infile=new File(source + ".vm");
		File outfile=new File(source + ".asm");
		CodeWriter cw = new CodeWriter(outfile);
		cw.writeInit();
		Parser parser = new Parser(infile);
		while(parser.hasMoreCommands()) {
			parser.advance();
			CommandType ct = parser.commandType();
			switch(ct) {
			case C_ARITHMETIC:
                final String command = parser.arg1();
                final ArithmeticCommandType arithmeticCommand = ArithmeticCommandType.arithmeticCommandType(command);
                cw.writeArithmetic(arithmeticCommand);
                break;
            case C_PUSH:
            case C_POP:
                final String segment = parser.arg1();
                MemorySegment memorySegment = MemorySegment.memSegment(segment);
                cw.writePushPop(ct, memorySegment, Integer.valueOf(parser.arg2()));
                break;
            case C_LABEL:
                cw.writeLabel(parser.arg1());
                break;
            case C_GOTO:
                cw.writeGoto(parser.arg1());
                break;
            case C_IF:
                cw.writeIf(parser.arg1());
                break;
            case C_FUNCTION:
                cw.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
                break;
            case C_CALL:
                cw.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
                break;
            case C_RETURN:
                cw.writeReturn();
                break;
			}
		}
		cw.close();
		System.out.println("Translation done.");
	}
}
