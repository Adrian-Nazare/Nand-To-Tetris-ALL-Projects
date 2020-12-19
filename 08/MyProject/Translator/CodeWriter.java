import java.io.File;
import java.io.PrintWriter;
import java.util.Stack;
import java.io.IOException;

class CodeWriter{
	
	final byte C_ARITHMETIC=0, C_PUSH=1, C_POP=2, C_LABEL=3, C_GOTO=4, C_IF=5, C_FUNCTION=6, C_RETURN=7, C_CALL=8;
	
	public int labelCounter; //we declare and later initialise a label counter which will keep track of each jump that will be used for the logical arithmetic operations
    public int functionCallCounter; //used to generate unique labels so that each function call of the same function can store its own return address
    public Stack<String> curentFunction; //we keep track of the current Function so that we can generate proper labels for the branching commands
    private String curentFile; //we keep track of the current file being processed in order to more easily identify potential issues
    PrintWriter writer;
    
    //constructor
    public CodeWriter(File outputFile){
        try {
            writer = new PrintWriter(outputFile);
            labelCounter = 0;
            functionCallCounter = 0; 
            curentFunction = new Stack<String>();
            curentFunction.push("default"); //used as a placeholder in case we are are translating a vm file without a function declaration
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    
    public void writeInit() {
    	writer.format("// BootstrapCode: Initialize SP pointer to 256 per convention & call Sys.init\n@256\nD=A\n@SP\nM=D\n");
    	writeFunctionCall("Sys.init", 0);
    }
    
    public void writeEndProgram() {
    	writer.format("// Infinite loop at the end of the program\n(END)\n@END\n0;JMP\n");
    }
    
    //byte c = command type, String a1 = arg1, String a2 = arg 2 
    public void writeArithmetic(String a1){
        switch (a1) {
            case "add":
                writer.format("// add\n@SP\nAM=M-1\nD=M\nA=A-1\nM=D+M\n\n");
                break;
            case "sub":
                writer.format("// sub\n@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n\n");
                break;
            case "neg":
                writer.format("// neg\n@SP\nA=M-1\nM=-M\n\n");
                break;
            case "eq":
                writer.format("// eq\n@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\nM=-1\n@TRUE%d\nD;JEQ\n@SP\nA=M-1\nM=0\n(TRUE%d)\n\n", labelCounter, labelCounter);
                labelCounter ++;
                break;
            case "gt":
                writer.format("// gt\n@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\nM=-1\n@TRUE%d\nD;JGT\n@SP\nA=M-1\nM=0\n(TRUE%d)\n\n", labelCounter, labelCounter);
                labelCounter ++;
                break;
            case "lt":
                writer.format("// lt\n@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\nM=-1\n@TRUE%d\nD;JLT\n@SP\nA=M-1\nM=0\n(TRUE%d)\n\n", labelCounter, labelCounter);
                labelCounter ++;
                break;
            case "and":
                writer.format("// and\n@SP\nAM=M-1\nD=M\nA=A-1\nM=D&M\n\n");
                break;
            case "or":
                writer.format("// or\n@SP\nAM=M-1\nD=M\nA=A-1\nM=D|M\n\n");
                break;
            case "not":
                writer.format("// neg\n@SP\nA=M-1\nM=!M\n\n");
                break;
            default:
                writer.format("Invalid command type provided \"%s\", continuing conversion...\n\n", a1);
                break;
        }
    }
    
    public void writePushPop(byte c, String a1, int a2){
        if (c == 1){    //if command type = C_PUSH = 1
            switch (a1){
                case "local":
                    writer.format("// push %s %d\n@%d\nD=A\n@LCL\nA=D+M\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, a2);
                    break;
                case "argument":
                    writer.format("// push %s %d\n@%d\nD=A\n@ARG\nA=D+M\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, a2);
                    break;
                case "this":
                    writer.format("// push %s %d\n@%d\nD=A\n@THIS\nA=D+M\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, a2);
                    break;
                case "that":
                    writer.format("// push %s %d\n@%d\nD=A\n@THAT\nA=D+M\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, a2);
                    break;
                case "temp":
                    writer.format("// push %s %d\n@%d\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, 5 + a2);
                    break;
                case "constant":
                    writer.format("// push %s %d\n@%d\nD=A\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, a2);
                    break;
                case "static":
                    writer.format("// push %s %d\n@%s.%d\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2, curentFile, a2);
                    break;
                case "pointer":
                    if (a2 == 0){
                        writer.format("// push %s %d\n@THIS\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2);
                        break;
                    }
                    else if (a2 == 1){
                        writer.format("// push %s %d\n@THAT\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n\n", a1, a2);
                        break;
                    }
                    else{
                        writer.format("Invalid pointer argument number provided \"%d\", continuing conversion...\n\n", a2);
                        break;    
                    }
                default:
                    writer.format("Invalid command type provided \"%d %s %d\", continuing conversion...\n\n", c, a1, a2);
                    break;
                    
            }
        }
        else if (c == 2){    //if command type = C_POP = 2
            switch (a1){
                case "local":
                    writer.format("// pop %s %d\n@%d\nD=A\n@LCL\nD=D+M\n@addr\nM=D\n@SP\nAM=M-1\nD=M\n@addr\nA=M\nM=D\n\n", a1, a2, a2);
                    break;
                case "argument":
                    writer.format("// pop %s %d\n@%d\nD=A\n@ARG\nD=D+M\n@addr\nM=D\n@SP\nAM=M-1\nD=M\n@addr\nA=M\nM=D\n\n", a1, a2, a2);
                    break;
                case "this":
                    writer.format("// pop %s %d\n@%d\nD=A\n@THIS\nD=D+M\n@addr\nM=D\n@SP\nAM=M-1\nD=M\n@addr\nA=M\nM=D\n\n", a1, a2, a2);
                    break;
                case "that":
                    writer.format("// pop %s %d\n@%d\nD=A\n@THAT\nD=D+M\n@addr\nM=D\n@SP\nAM=M-1\nD=M\n@addr\nA=M\nM=D\n\n", a1, a2, a2);
                    break;
                case "temp":
                    writer.format("// pop %s %d\n@SP\nAM=M-1\nD=M\n@%d\nM=D\n\n", a1, a2, 5 + a2);
                    break;
                case "static":
                    writer.format("// pop %s %d\n@SP\nAM=M-1\nD=M\n@%s.%d\nM=D\n\n", a1, a2, curentFile, a2);
                    break;
                case "pointer":
                    if (a2 == 0){
                        writer.format("// pop %s %d\n@SP\nAM=M-1\nD=M\n@THIS\nM=D\n\n", a1, a2);
                        break;
                    }
                    else if (a2 == 1){
                        writer.format("// pop %s %d\n@SP\nAM=M-1\nD=M\n@THAT\nM=D\n\n", a1, a2);
                        break;
                    }
                    else{
                        writer.format("Invalid pointer argument number provided \"%d\", continuing conversion...\n\n", a2);
                        break;    
                    }
                default:
                    writer.format("Invalid command type provided \"%d %s %d\", continuing conversion...\n\n", c, a1, a2);
                    break;
            }
        }
        else{
            writer.format("Invalid Push/Pop command provided \"%d %s %d\", continuing conversion...\n\n", c, a1, a2);
            return;
        }
    }
    
    
    public void writeBranching(byte c, String a1) {
    	switch (c){
        	case C_LABEL:
        		writer.format("// label %s\n(LABEL_%s:%s)\n\n", a1, curentFunction.peek(), a1);
        		break;
        	case C_GOTO:
        		writer.format("//goto %s\n@LABEL_%s:%s\n0;JMP\n\n", a1, curentFunction.peek(), a1);
        		break;
        	case C_IF:
        		writer.format("//if-goto %s\n@SP\nAM=M-1\nD=M\n@LABEL_%s:%s\nD;JNE\n\n", a1, curentFunction.peek(), a1);
        		break;
        	default:
                writer.format("Invalid command type provided \"%d %s \", continuing conversion...\n\n", c, a1);
                break;
    	}
    }
    
    public void writeFunctionCall(String a1, int a2) {
    	writer.format("// call %s %d\n"
    			+ "// push return-address\n@RETURN_%s:%d\nD=A\n@SP\nM=M+1\nA=M-1\nM=D\n"
    			+ "// push LCL\n@LCL\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n"
    			+ "// push ARG\n@ARG\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n"
    			+ "// push THIS\n@THIS\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n"
    			+ "// push THAT\n@THAT\nD=M\n@SP\nM=M+1\nA=M-1\nM=D\n"
    			+ "// ARG=SP-n-5\n@%d\nD=A\n@SP\nD=M-D\n@ARG\nM=D\n"
    			+ "// LCL=SP\n@SP\nD=M\n@LCL\nM=D\n"
    			+ "// goto f\n@FUNCTION_%s\n0;JMP\n(RETURN_%s:%d)\n\n", 
    			a1, a2, a1, functionCallCounter, (5 + a2), a1, a1, functionCallCounter);
    	
    	functionCallCounter++;
  	}
    
    
    public void writeFunction(String a1, int a2) {
    	curentFunction.push(a1);
    	writer.format("// function %s %d\n(FUNCTION_%s)\n", a1, a2, a1);
    	for (int i = 0; i < a2; i++) {
    		writer.format("@SP\nM=M+1\nA=M-1\nM=0\n");
    	}
    	writer.format("\n");
    }
    
    public void writeReturn() {
    	writer.format("// return\n"
    			+ "// FRAME=LCL\n@LCL\nD=M\n@FRAME\nM=D\n"
    			+ "// RET=*(FRAME-5)\n@5\nA=D-A\nD=M\n@RET\nM=D\n"
    			+ "// *ARG=pop()\n@SP\nA=M-1\nD=M\n@ARG\nA=M\nM=D\n"
    			+ "// SP=ARG+1\nD=A+1\n@SP\nM=D\n"
    			+ "// THAT=*(FRAME-1)\n@FRAME\nAM=M-1\nD=M\n@THAT\nM=D\n"
    			+ "// THIS=*(FRAME-2)\n@FRAME\nAM=M-1\nD=M\n@THIS\nM=D\n"
    			+ "// ARG=*(FRAME-3)\n@FRAME\nAM=M-1\nD=M\n@ARG\nM=D\n"
    			+ "// LCL=*(FRAME-4)\n@FRAME\nAM=M-1\nD=M\n@LCL\nM=D\n"
    			+ "// goto RET\n@RET\nA=M\n0;JMP\n\n");
    }
    
    
    public void setFileName(String fileName) {
    	curentFile = fileName;
    }
    
    
    public void close (){
        writer.close();
        System.out.println("Output file closed successfully;");
    }
    
}
