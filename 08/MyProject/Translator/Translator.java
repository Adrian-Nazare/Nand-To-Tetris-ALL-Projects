import java.io.File;
import java.util.List;
import java.util.ArrayList;



public class Translator{
    
    public static void main(String args[]){
    	
        //checks for the correct number of arguments
        if (args.length != 1){
            System.out.println (String.format("Invalid number of arguments passed: %d.\nPlease pass 1 argument, which is input xxx.vm for output xxx.asm", args.length));
            return;
        }
        
        //creates an initially empty list of files to translate
        List<String> files = new ArrayList<String>();
        //hands the input over to listFilesforFolder method, which checks extensions and appends the list with all the files that will be translated
        files.addAll(listFilesforFolder(new File (args[0])));
        
        //return if a non-vm file or a folder without valid vm files was provided
        if (files.size() > 0)
        	System.out.println(String.format("Files to be translated: %s", files));
        else {
        	System.out.println("no files found, exiting program");
        	return;
        }        	

        
        //declare the variables that will be used for command Type, argument 1 and argument 2
        final byte C_ARITHMETIC=0, C_PUSH=1, C_POP=2, C_LABEL=3, C_GOTO=4, C_IF=5, C_FUNCTION=6, C_RETURN=7, C_CALL=8;
        byte c; String a1; int a2;
        
        //output file will be named after the directory, or the single vm file that was provided
        File outputFile;
        CodeWriter codeWriter;
        
        if ((new File (args[0])).isFile()) {
        	outputFile = new File (String.format("%s.asm", args[0].substring(0, args[0].lastIndexOf("."))));
        	codeWriter = new CodeWriter(outputFile);
        }
        else {
        	outputFile = new File (String.format("%s.asm", args[0]));
        	codeWriter = new CodeWriter(outputFile);
        	//calls writeInit to write the bootstrap code at the beginning of the output file
        	codeWriter.writeInit();
        }
        
        //we go through parsing each file
        for (String filesEntry : files) {
        	File inputFile;
        	if ((new File (args[0])).isDirectory())
        		//we append the path name to the name of each file entry in the directory, so that it is found
            	inputFile = new File (String.format("%s\\%s", args[0], filesEntry));
        	else 
            	inputFile = new File (filesEntry);

        	
            Parser parser = new Parser(inputFile);
            //we let the codeWriter know which file it is currently processing
            codeWriter.setFileName(filesEntry.substring(0, filesEntry.lastIndexOf(".")));
            
            System.out.println(String.format("Translating file \"%s\"...", filesEntry));
            while (parser.hasMoreCommands()){ //checking if there is a valid current command
                c = parser.returnCommandType();
                a1= parser.returnArg1(); //a previous arg1 is returned if the command is C_RETURN, but it is not used
                
                //depending on the command type, we get the rest of the necessary arguments and hand them over to the codeWriter to be translated
                if (c == C_ARITHMETIC)
                    codeWriter.writeArithmetic(a1);
                else if (c == C_PUSH || c == C_POP){
                    a2 = parser.returnArg2();
                    codeWriter.writePushPop(c, a1, a2);
                }
                else if (c == C_LABEL || c == C_GOTO || c == C_IF) {
                	codeWriter.writeBranching(c, a1);
                }
                else if (c == C_CALL) {
                	a2 = parser.returnArg2();
                	codeWriter.writeFunctionCall(a1, a2);
                }
                else if (c == C_FUNCTION) {
                	a2 = parser.returnArg2();
                	codeWriter.writeFunction(a1, a2);
                }
                else if (c == C_RETURN) {
                	codeWriter.writeReturn();
                }
                else{
                    System.out.println(String.format("Invalid command provided in input file line %d: \"%s\", continuing", parser.lineCounter, parser.curentLine));
                }
                parser.advance();
            }
            System.out.println(String.format("End of input file \"%s\" reached, attempting to close...", inputFile));
            parser.close();
        }
        
        codeWriter.writeEndProgram();
        System.out.println(String.format("Attempting to close output file \"%s\"...", outputFile));
        codeWriter.close(); 
    }
    
    /*method to return a list of input files to be translated, depending on the specified command line argument
      list contains 1 file if a .vm file was specified
      list contains any number of files if a directory with more files was specified
      list contains 0 files if an incorrect file, or a directory without .vm files was provided, in which case program will terminate
    */
    public static List<String> listFilesforFolder (final File fileOrDir){
    	List<String> files = new ArrayList<String>();
    	
    	if (fileOrDir.isFile()) {
    		//if the input is a file, check if it is a .vm file and add it to the list, returning it afterwards
    		if (fileOrDir.getName().endsWith(".vm")) {
    			files.add(String.format("%s", fileOrDir.getName()));
    			return files;
    		}
    		else {
    			System.out.println(String.format("Invalid filetype provided in file %s, returning", fileOrDir.getName()));
    			return files;
    		}
    	}
    	
    	else if (fileOrDir.isDirectory()) {
    		//if the input is a directory, we go through each entry to check if it is a file, and if it has the .vm extension 
    		for (final File fileEntry : fileOrDir.listFiles()) {
        		if (fileEntry.isFile() && fileEntry.getName().endsWith(".vm"))
        			files.add(fileEntry.getName());
        		else 
        			//we skip all invalid files, announcing as such 
        			System.out.println(String.format("invalid file found at %s\\%s, skippping...", fileOrDir.getName(), fileEntry.getName()));
        	}
        	return files;
    	}
    	else {
    		System.out.println(String.format("Invalid filetype provided in file %s, returning", fileOrDir.getName()));
    		return files;
    	}
    	    	
    }
    
}




















