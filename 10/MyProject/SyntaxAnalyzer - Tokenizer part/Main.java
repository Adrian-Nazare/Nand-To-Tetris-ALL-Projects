import java.io.File;
import java.util.List;
import java.util.ArrayList;



public class Main{
    
    public static void main(String args[]){

        //checks for the correct number of arguments
        if (args.length != 1){
            System.out.println (String.format("Invalid number of arguments passed: %d.\nPlease pass 1 argument, "
            		+ "which is input xxx.jack for output xxx.jack, or a directory containing jack files", args.length));
            return;
        }     
        
        //creates an initially empty list of files to translate
        List<String> files = new ArrayList<String>();
        //hands the input over to listFilesforFolder method, which checks extensions and appends the list with all the files that will be translated
        files.addAll(listFilesforFolder(new File (args[0])));
        
        //return if a non-jack file or a folder without valid jack files was provided
        if (files.size() > 0)
        	System.out.println(String.format("Files to be translated: %s\n", files));
        else {
        	System.out.println("no files found, exiting program");
        	return;
        }        	

        
        //output file will be named after the directory, or the single jack file that was provided
        File inputFile;
        File outputFile;
        
        CompilationEngine compilationEngine;    
        
        //we go through parsing each file
        for (String filesEntry : files) {
        	
        	if ((new File (args[0])).isDirectory()) {
        		//we append the path name to the name of each file entry in the directory, so that it is found
            	inputFile = new File (String.format("%s\\%s", args[0], filesEntry));
        		outputFile = new File (String.format("%s\\%s", args[0], String.format("%s.xml", filesEntry.substring(0, filesEntry.lastIndexOf(".")))) );
        	}
        	else {
            	inputFile = new File (filesEntry);
        		outputFile = new File (String.format("%s.xml", filesEntry.substring(0, filesEntry.lastIndexOf("."))));
        	}

            compilationEngine = new CompilationEngine(inputFile, outputFile);
            compilationEngine.run();
            
            System.out.println(String.format("Attempting to close output file \"%s\"...", outputFile));
            compilationEngine.close(); 
            System.out.println(String.format("Output file \"%s\" closed successfully\n", outputFile));
        }
        System.out.println("End of files reached, program completed successfully!");
       
    }
    
    /*method to return a list of input files to be translated, depending on the specified command line argument
      list contains 1 file if a .jack file was specified
      list contains any number of files if a directory with more files was specified
      list contains 0 files if an incorrect file, or a directory without .jack files was provided, in which case program will terminate
    */
    public static List<String> listFilesforFolder (final File fileOrDir){
    	List<String> files = new ArrayList<String>();
    	
    	if (fileOrDir.isFile()) {
    		//if the input is a file, check if it is a .jack file and add it to the list, returning it afterwards
    		if (fileOrDir.getName().endsWith(".jack")) {
    			files.add(String.format("%s", fileOrDir.getName()));
    			return files;
    		}
    		else {
    			System.out.println(String.format("Invalid filetype provided in file %s, returning", fileOrDir.getName()));
    			return files;
    		}
    	}
    	
    	else if (fileOrDir.isDirectory()) {
    		//if the input is a directory, we go through each entry to check if it is a file, and if it has the .jack extension 
    		for (final File fileEntry : fileOrDir.listFiles()) {
        		if (fileEntry.isFile() && fileEntry.getName().endsWith(".jack"))
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




















