import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//Parser coded to ignore all white-spaces, except in lines with function calls and declarations
class Parser{
    
	final byte C_ARITHMETIC=0, C_PUSH=1, C_POP=2, C_LABEL=3, C_GOTO=4, C_IF=5, C_FUNCTION=6, C_RETURN=7, C_CALL=8;
	
    private byte commandType;
    private String arg1;
    private int arg2;
    
    public String inputFileName; //used for diagnostic purposes in case an invalid command is encountered
    public int lineCounter; //used for diagnostic purposes in case an invalid command is encountered
        
    public String curentLine;
    private String nextLine;
    private String stripped; //variable used to modify the line and parse it into its command components
    
    //lists used for easier checking of arithmetic commands and push/pop command segments
    private String[] arithmeticList = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};
    private String[] pushPopCommandsList = {"local", "argument", "this", "that", "constant", "static", "pointer", "temp"};
    
    //we use a BufferedReader object in order to read the file one line at a time
    BufferedReader reader = null; 
    
    //constructor
    public Parser(File inputFile){
        try {
            
            reader = new BufferedReader(new FileReader(inputFile));
            
            inputFileName = inputFile.getName();
            curentLine = reader.readLine();
            stripped = curentLine;
            nextLine = reader.readLine();
            
            lineCounter = 1;
                        
        } catch (IOException e) {
            e.printStackTrace();
        }
        //if the input file was successfully opened, the constructor will attempt to automatically update the commands starting with the first line read
        updateCommands();
    }
    
    public void advance(){
        curentLine = nextLine;
        stripped = curentLine;
        try {nextLine = reader.readLine();
            } catch (IOException e) {e.printStackTrace();} 
        lineCounter ++;
        updateCommands();
    }
    
    public boolean hasMoreCommands () {
        if (curentLine == null)
            return false;
        else
            return true;
    }
    
    private void updateCommands(){
        //Check if we have any line to parse at all, else the method returns here
    	arg2 = 0;
    	if (stripped == null)
    		return;
    	
        //Prepare the string for processing
    	stripped = stripped.replaceAll("\\s+","");
        if (stripped.startsWith("//") || stripped.isEmpty()){
            advance();
            return;
        }
        stripped = stripped.split("//", 2)[0];
        stripped = stripped.toLowerCase();
        
        //UpdateCommands returns when one of the parsing methods returns true, as in having identified as well as successfully parsed a command
        //if no parsing methods return true (an invalid command provided), UpdateCommands puts out an error message and calls UpdateCommands again in order to attempt to parse the next line
        if (isProcessArithmetic()){
            return;
        }
                
        else if (isProcessPushPop()){
            if (isProcessPushPopCommand()){
                if (isProcessPushPopArg2Numeric())
                    return;
            }
        }
                
        else if (isProcessBranching()){
            return;
        }
        
        /*if the command was a function call or function declaration, Update commands does not use the previously prepared string (which had all characters made lower-case, and all white-spaces removed)
        instead, the current line is cleaned of comments and tabs, and split into an array of substrings that were separated by white-spaces
        this is done in order to preserve the names of functions, which can include upper case letters and numbers that could be confused with the numerical argument nArgs or nVars*/
        else if (isProcessCallFunc()){
        	if (isProcessFuncCommandAndNumArgs(curentLine.split("//", 2)[0].trim().replaceAll("\\t", "").split(" ", 3)))
            	return;
        }
        
        else if (isProcessFunc()){
        	if (isProcessFuncCommandAndNumArgs(curentLine.split("//", 2)[0].trim().replaceAll("\\t", "").split(" ", 3)))
            	return;
        }
                
        else if (isProcessReturn()) {
        	return;
        } 
        
        else {
            System.out.println(String.format("Invalid command provided in input file \"%s\" at line %d: \"%s\", continuing", inputFileName, lineCounter, curentLine));
        	advance(); 
            return;
        }       
    }
    
  
    //if command is C-Arithmetic, update argument1 and return true, else return false
    private boolean isProcessArithmetic(){
        for (String substring: arithmeticList){
            if (stripped.startsWith(substring)){
                commandType = C_ARITHMETIC; //C_ARITHMETIC = 0
                arg1 = substring;
                return true;
            }
        }
        return false;
    }
    
    
    //if command is of type branching, update the command type and argument1 and return true, else return false
    private boolean isProcessBranching(){
        if (stripped.startsWith("label")){
            commandType = C_LABEL; //C_LABEL=3
            arg1 = stripped.substring(5);
            return true;
        }
        if (stripped.startsWith("goto")){
            commandType = C_GOTO; //C_GOTO=4
            arg1 = stripped.substring(4);
            return true;
    	}
        if (stripped.startsWith("if-goto")){
            commandType = C_IF; //C_IF=5
            arg1 = stripped.substring(7);
            return true;
    	}
    	return false;
    }
    
    //if command is C_FUNCTION, update the command type and return true, the UpdateCommands Method will see if the command has a valid arg1 and arg2 by calling isProcessFuncCommandAndNumArgs(String[])
    private boolean isProcessFunc(){
        if (stripped.startsWith("function")){
                commandType = C_FUNCTION; //C_FUNCTION = 6
                stripped = stripped.substring(8);
                return true;
        }
        return false;
    }
    
    //if command is C_CALL, update the command type and return true, the UpdateCommands Method will see if the command has a valid arg1 and arg2 by calling isProcessFuncCommandAndNumArgs(String[])
    private boolean isProcessCallFunc(){
        if (stripped.startsWith("call")){
                commandType = C_CALL; //C_CALL = 8
                stripped = stripped.substring(4);
                return true;
        }
        return false;
    }
    
    //called if command is C_FUNCITON or C_CALL, updates arg1 and arg2 if the function call or declaration was correctly written and returns true, else returns false
    private boolean isProcessFuncCommandAndNumArgs(String[] arguments){
    	if (arguments.length < 3) {
    		System.out.println(String.format("Incorect function declaration in input file \"%s\" at line %d: \"%s\", continuing", inputFileName, lineCounter, curentLine));
    		return false;
    	}
    	else {
    		arg1 = arguments[1];
    		try {
    			arg2 = Integer.parseInt(arguments[2]);
    		} catch (NumberFormatException n) {
    			System.out.println(String.format("Could not find numeric argument in function declaration in input file \"%s\" at line %d: \"%s\", continuing", inputFileName, lineCounter, curentLine));
            	return false;
    		}
    		return true;
    	}
    		
    }
    
    //updates commandType to C_RETURN and returns true if command started with "return"
    private boolean isProcessReturn(){
        if (stripped.startsWith("return")){
                commandType = C_RETURN; //C_RETURN = 7
                return true;
        }
        return false;
    }
    
    //if we have a push or pop command, this method updates commandType and prepares the command (or stripped string) to be processed by isProcessPushPopCommand(), returning true, else returns false
    private boolean isProcessPushPop(){
        if (stripped.startsWith("push")){
                commandType = C_PUSH; //C_PUSH=1
                stripped = stripped.substring(4);
                return true;
        }
        if (stripped.startsWith("pop")){
                commandType = C_POP; //C_POP=2
                stripped = stripped.substring(3);
                return true;
        }
        return false;
    }
    
    //for push/pop command, looks to see if it contains a valid segment as the first argument. If so: updates arg1, prepares the string to be processed by private boolean isProcessPushPopArg2Numeric() and returns true, else returns false
    private boolean isProcessPushPopCommand(){
        for (String substring: pushPopCommandsList){
            if (stripped.startsWith(substring)){
                arg1 = substring;
                stripped = stripped.substring(substring.length());
                return true;
            }
        }
        System.out.println(String.format("Unsuitable segment declared for push/pop command in input file \"%s\" at line %d: \"%s\", continuing", inputFileName, lineCounter, curentLine));
        return false;
    }
    
    //for push/pop command with a valid segment as arg1, checks to see if the 3rd argument is numeric, and parses the numbers into the arg2 integer, returning true. else returns false
    private boolean isProcessPushPopArg2Numeric(){
    	if (stripped.length() > 0 ){
        	if (Character.isDigit(stripped.charAt(0))){
            	while (stripped.length() > 0 ) {
            		if (Character.isDigit(stripped.charAt(0))) {
            			arg2 = arg2 * 10 + Character.getNumericValue(stripped.charAt(0));
            			stripped = stripped.substring(1);
            		}
            	}
            	return true;
        	}
            System.out.println(String.format("No numeric argument declared for push/pop command in input file \"%s\" at line %d: \"%s\", continuing", inputFileName, lineCounter, curentLine));
        	return false;
    	}
        System.out.println(String.format("No numeric argument declared for push/pop command in input file \"%s\" at line %d: \"%s\", continuing", inputFileName, lineCounter, curentLine));
    	return false;
    }
    
    
    public byte returnCommandType(){
        return commandType;
    }
    
    
    public String returnArg1(){
        return arg1;
    }
    
    
    public int returnArg2(){
        return arg2;
    }
    
    
    public void close(){
        try {
        	reader.close();
        	System.out.println(String.format("Input file \"%s\" closed successfully", inputFileName));
        } catch (IOException logOrIgnore) {}
    }
}