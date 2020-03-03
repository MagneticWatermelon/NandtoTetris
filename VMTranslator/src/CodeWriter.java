import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
	private PrintWriter pw = null;
	private String infilename = null;
	public CodeWriter(File outfile) {
		try {
			this.pw = new PrintWriter(outfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void writeArithmetic(ArithmeticCommandType command) throws IOException {
        switch (command) {
            case ADD:
                writeAdd();
                break;
            case SUB:
                writeSub();
                break;
            case NEG:
                writeNeg();
                break;
            case EQ:
                writeEQ();
                break;
            case GT:
                writeGT();
                break;
            case LT:
                writeLT();
                break;
            case AND:
                writeAnd();
                break;
            case OR:
                writeOr();
                break;
            case NOT:
                writeNot();
                break;
        }
    }
	public void writePushPop(CommandType commandType, MemorySegment memorySegment, int index) {
        switch (commandType) {
            case C_PUSH:
                writePush(memorySegment, index);
                break;
            case C_POP:
                writePop(memorySegment, index);
                break;
        }
    }
	public void writeInit() {
        pw.println("@256");
        pw.println("D = A");
        pw.println("@SP");
        pw.println("M = D");
        writeCall("Sys.init", 0);
    }
    public void writeCall(String functionname, int args) {
    	String returnAddress = String.valueOf(System.currentTimeMillis());
    	// Push return address
        pw.println("@" + returnAddress);
        pw.println("D = A");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();
 
        // Push LCL
        pw.println("@LCL");
        pw.println("D = M");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();
 
        // Push ARG
        pw.println("@ARG");
        pw.println("D = M");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();
 
        // Push THIS
        pw.println("@THIS");
        pw.println("D = M");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();
 
        // Push THAT
        pw.println("@THAT");
        pw.println("D = M");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();
 
        // ARG = SP - n - 5
        pw.println("@SP");
        pw.println("D = M");
        pw.println("@" + args);
        pw.println("D = D - A");
        pw.println("@5");
        pw.println("D = D - A");
        pw.println("@ARG");
        pw.println("M = D");
 
        // LCL = SP ie. reposition LCL
        pw.println("@SP");
        pw.println("D = M");
        pw.println("@LCL");
        pw.println("M = D");
 
        // goto functionName
        writeGoto(functionname);
        writeLabel(returnAddress);
	}
    public void writeReturn() {
        // FRAME is a temporary variable
        // FRAME == R13
        pw.println("@LCL");
        pw.println("D = M");
        pw.println("@R13");
        pw.println("M = D");
 
        // Put the return address in a temp variable (R14)
        // (RETADDR == *(FRAME - 5)) = (RETADDR == R14)
        pw.println("@R13");
        pw.println("D = M");
        pw.println("@5");
        pw.println("D = D - A");
        pw.println("A = D");
        pw.println("D = M");
        pw.println("@R14");
        pw.println("M = D");
 
        // Copy return value to D
        pw.println("@SP");
        pw.println("A = M");
        pw.println("A = A - 1");
        pw.println("D = M");
 
        pw.println("@ARG");
        pw.println("A = M");
        pw.println("M = D");
 
        // Restore Stack Pointer of the caller
        // SP = ARG + 1
        pw.println("@ARG");
        pw.println("D = M");
        pw.println("D = D + 1");
        pw.println("@SP");
        pw.println("M = D");
 
        // THAT = *(FRAME - 1)
        pw.println("@R13");
        pw.println("D = M");
        pw.println("@1");
        pw.println("D = D - A");
        pw.println("A = D");
        pw.println("D = M");
        pw.println("@THAT");
        pw.println("M = D");
 
        // THIS = *(FRAME - 2)
        pw.println("@R13");
        pw.println("D = M");
        pw.println("@2");
        pw.println("D = D - A");
        pw.println("A = D");
        pw.println("D = M");
        pw.println("@THIS");
        pw.println("M = D");
 
        // ARG = *(FRAME - 3)
        pw.println("@R13");
        pw.println("D = M");
        pw.println("@3");
        pw.println("D = D - A");
        pw.println("A = D");
        pw.println("D = M");
        pw.println("@ARG");
        pw.println("M = D");
 
        // LCL = *(FRAME - 4)
        pw.println("@R13");
        pw.println("D = M");
        pw.println("@4");
        pw.println("D = D - A");
        pw.println("A = D");
        pw.println("D = M");
        pw.println("@LCL");
        pw.println("M = D");
 
        // goto RET
        pw.println("@R14");
        pw.println("A = M");
        pw.println("0;JMP");
    }
	public void writeLabel(String label) {
        pw.println("(" + label + ")");
    }
    public void writeGoto(String label) {
        unCondJumpToLabel(label);
    }
	private void unCondJumpToLabel(String label) {
		pw.println("@" + label);
        pw.println("0;JMP");		
	}
	private void writePop(MemorySegment memorySegment, int index) {
		decrementStackPointer();
        switch (memorySegment) {
            case LOCAL:
                writePopForMemoryLocation(index, "@LCL");
                break;
            case ARGUMENT:
                writePopForMemoryLocation(index, "@ARG");
                break;
            case THIS:
                writePopForMemoryLocation(index, "@THIS");
                break;
            case THAT:
                writePopForMemoryLocation(index, "@THAT");
                break;
            case STATIC:
                popStackValueToRegisterD();
                pw.println("@" + infilename + "." + index);
                pw.println("M = D");
                break;
            case POINTER:
                popStackValueToRegisterD();
                if (index == 0) {
                    pw.println("@THIS");
                }
                if (index == 1) {
                    pw.println("@THAT");
                }
                pw.println("M = D");
                break;
            case TEMP:
                popStackValueToRegisterD();
                pw.println("@" + (index + 5));
                pw.println("M = D");
                break;
        }		
	}
	private void writePopForMemoryLocation(int index, String memloc) {
		pw.println(memloc);
        pw.println("D = M");
        pw.println("@" + index);
        pw.println("D = D + A");
        pw.println(memloc);
        pw.println("M = D");
        popStackValueToRegisterD();
        pw.println(memloc);
        pw.println("A = M");
        pw.println("M = D");
        pw.println(memloc);
        pw.println("D = M");
        pw.println("@" + index);
        pw.println("D = D - A");
        pw.println(memloc);
        pw.println("M = D");
		
	}
	private void writePush(MemorySegment memorySegment, int index) {
		switch (memorySegment) {
        case CONSTANT:
            pw.println("@" + index);
            pw.println("D = A");
            loadStackAddressToRegisterA();
            pw.println("M = D");
            break;
        case LOCAL:
            writePushForMemoryLocation("@LCL", index);
            break;
        case ARGUMENT:
            writePushForMemoryLocation("@ARG", index);
            break;
        case THIS:
            writePushForMemoryLocation("@THIS", index);
            break;
        case THAT:
            writePushForMemoryLocation("@THAT", index);
            break;
        case STATIC:
            pw.println("@" + infilename + "." + index);
            pw.println("D = M");
            loadStackAddressToRegisterA();
            pw.println("M = D");
            break;
        case POINTER:
            // push THIS or THAT pointer (pointer itself) to stack.
            if (index == 0) {
                pw.println("@THIS");
            }
            if (index == 1) {
                pw.println("@THAT");
            }
            pw.println("D = M");
            loadStackAddressToRegisterA();
            pw.println("M = D");
            break;
        case TEMP:
            // TEMP is not a pointer, general purpose data register.
            // TEMP is between R5 - R12
            pw.println("@" + (5 + index));
            pw.println("D = M");
            loadStackAddressToRegisterA();
            pw.println("M = D");
            break;
		}
		incrementStackPointer();
		
	}
	private void writePushForMemoryLocation(String memloc, int index) {
		// Increment memory location base address by index.
        pw.println(memloc);
        pw.println("D = M");
        pw.println("@" + index);
        pw.println("D = D + A");
        pw.println(memloc);
        pw.println("M = D");
 
        // D = *memoryLocation
        pw.println(memloc);
        pw.println("A = M");
        pw.println("D = M");
 
        // *SP = D
        loadStackAddressToRegisterA();
        pw.println("M = D");
 
        // Reset memory location base address.
        pw.println(memloc);
        pw.println("D = M");
        pw.println("@" + index);
        pw.println("D = D - A");
        pw.println(memloc);
        pw.println("M = D");
		
	}
	public void writeIf(String label) {
        decrementStackPointer();
        popStackValueToRegisterD();
        pw.println("@" + label);
        pw.println("D;JNE");
    }
    public void writeFunction(String functionName, int numLocals) {
        writeLabel(functionName);
        for (int i = 0; i < numLocals; i++) {
            loadStackAddressToRegisterA();
            pw.println("M = 0");
            incrementStackPointer();
        }
    }
	private void writeNot() {
		decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("M = !M");
        incrementStackPointer();
		
	}
	private void writeOr() {
		decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("M = D | M");
        incrementStackPointer();
		
	}
	private void writeAnd() {
		decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("M = D & M");
        incrementStackPointer();
		
	}
	private void writeLT() {
		String uniqueLabel = String.valueOf(System.currentTimeMillis());
		String uniqueLabelTrue = uniqueLabel + ".true";
        String uniqueLabelFalse = uniqueLabel + ".false";
        String uniqueLabelEnd = uniqueLabel + ".end";
        // true : -1
        // false: 0
        decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("D = M - D");
        pw.println("@" + uniqueLabelTrue);
        pw.println("D;JLT");
        pw.println("@" + uniqueLabelFalse);
        pw.println("D;JGE");
        pw.println("(" + uniqueLabelTrue + ")");
        loadStackAddressToRegisterA();
        setSPToTrue();
        unCondJumpToLabel(uniqueLabelEnd);
        pw.println("(" + uniqueLabelFalse + ")");
        loadStackAddressToRegisterA();
        pw.println("M = 0"); // set *SP to false
        unCondJumpToLabel(uniqueLabelEnd);
        pw.println("(" + uniqueLabelEnd + ")");
        incrementStackPointer();
		
	}
	private void writeGT() {
		String uniqueLabel = String.valueOf(System.currentTimeMillis());
		String uniqueLabelTrue = uniqueLabel + ".true";
        String uniqueLabelFalse = uniqueLabel + ".false";
        String uniqueLabelEnd = uniqueLabel + ".end";
        // true : -1
        // false: 0
        decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("D = M - D");
        pw.println("@" + uniqueLabelTrue);
        pw.println("D;JGT");
        pw.println("@" + uniqueLabelFalse);
        pw.println("D;JLE");
        pw.println("(" + uniqueLabelTrue + ")");
        loadStackAddressToRegisterA();
        setSPToTrue();
        unCondJumpToLabel(uniqueLabelEnd);
        pw.println("(" + uniqueLabelFalse + ")");
        loadStackAddressToRegisterA();
        pw.println("M = 0");
        unCondJumpToLabel(uniqueLabelEnd);
        pw.println("(" + uniqueLabelEnd + ")");
        incrementStackPointer();
		
	}
	private void writeEQ() {
		String uniqueLabel = String.valueOf(System.currentTimeMillis());
		String uniqueLabelTrue = uniqueLabel + ".true";
        String uniqueLabelFalse = uniqueLabel + ".false";
        String uniqueLabelEnd = uniqueLabel + ".end";
        // true : -1
        // false: 0
        decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("D = D - M");
        pw.println("@" + uniqueLabelTrue);
        pw.println("D;JEQ");
        pw.println("@" + uniqueLabelFalse);
        pw.println("D;JNE");
        pw.println("(" + uniqueLabelTrue + ")");
        loadStackAddressToRegisterA();
        setSPToTrue();
        unCondJumpToLabel(uniqueLabelEnd);
        pw.println("(" + uniqueLabelFalse + ")");
        loadStackAddressToRegisterA();
        pw.println("M = 0");
        unCondJumpToLabel(uniqueLabelEnd);
        pw.println("(" + uniqueLabelEnd + ")");
        incrementStackPointer();
		
	}
	private void setSPToTrue() {
		pw.println("M = 1");
        pw.println("M = -M"); // set *SP to true		
	}
	private void writeNeg() {
		decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("M = -M");
        incrementStackPointer();
		
	}
	private void writeSub() {
		decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("D = D - M");
        pw.println("D = -D");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();
		
	}
	private void writeAdd() {
		decrementStackPointer();
        popStackValueToRegisterD();
        decrementStackPointer();
        loadStackAddressToRegisterA();
        pw.println("D = D + M");
        loadStackAddressToRegisterA();
        pw.println("M = D");
        incrementStackPointer();		
	}
	private void incrementStackPointer() {
		pw.println("@SP");
        pw.println("M = M + 1");		
	}
	private void loadStackAddressToRegisterA() {
		pw.println("@SP");
        pw.println("A = M");
	}
	private void popStackValueToRegisterD() {
		loadStackAddressToRegisterA();
        pw.println("D = M");
	}
	private void decrementStackPointer() {
		pw.println("@SP");
        pw.println("M = M - 1");
	}
	public void close() throws IOException {
        pw.flush();
        pw.close();
    }
	public void setSourceFileName(String inFileName) {
        this.infilename = inFileName;
    }
}
