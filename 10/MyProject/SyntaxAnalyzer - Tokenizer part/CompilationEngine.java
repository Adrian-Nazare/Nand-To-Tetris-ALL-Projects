import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CompilationEngine {
	Tokenizer tokenizer;
    PrintWriter writer;
    
    int indentation;
    
    final int KEYWORD=0, SYMBOL=1, IDENTIFIER=2, INT_CONST=3, STRING_CONST=4;
    final int CLASS=5, CONSTRUCTOR=6, FUNCTION=7, METHOD=8, FIELD=9, STATIC=10, 
    		  VAR=11, INT=12, CHAR=13, BOOLEAN=14, VOID=15, TRUE=16, FALSE=17, 
    		  NULL=18, THIS=19, LET=20, DO=21, IF=22, ELSE=23, WHILE=24, RETURN=25;
    
    String inputFileName;
	
	//constructor
	public CompilationEngine(File inputFile, File outputFile) {
        try {
            writer = new PrintWriter(outputFile);    
    		tokenizer = new Tokenizer(inputFile);
    		inputFileName = inputFile.getName();
    		
        } catch (IOException e) { e.printStackTrace(); } 

	}
	
	public void run() {
		writer.format("<tokens>\n");
		while (tokenizer.hasMoreCommands()) { //checking if there is a valid current command
            //TO DO
			writer.format("<%s> %s </%s>\n", tokenizer.getTokenLabel(), tokenizer.getToken(), tokenizer.getTokenLabel());
		//	System.out.println(String.format("<%s>%s</%s>\n", tokenizer.getTokenLabel(), tokenizer.getToken(), tokenizer.getTokenLabel()));
			tokenizer.advance();
        }
		writer.format("</tokens>");
		tokenizer.close();
	}
	
	public void close() {
		writer.close();
	}
}