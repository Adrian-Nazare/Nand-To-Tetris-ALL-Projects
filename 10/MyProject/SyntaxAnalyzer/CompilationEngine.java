import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CompilationEngine {
	//Declaring & initialising the constants 
    final int KEYWORD=0, SYMBOL=1, INT_CONST=2, STRING_CONST=3, IDENTIFIER=4;
    final int CLASS=5, CONSTRUCTOR=6, FUNCTION=7, METHOD=8, FIELD=9, STATIC=10, 
    		  VAR=11, INT=12, CHAR=13, BOOLEAN=14, VOID=15, TRUE=16, FALSE=17, 
    		  NULL=18, THIS=19, LET=20, DO=21, IF=22, ELSE=23, WHILE=24, RETURN=25;
    char[] op = {'+', '-', '*', '/', '&', '|', '<', '>', '='};
    
	Tokenizer tokenizer;
    PrintWriter writer;
    
    int index, indentation; //Used for indenting each token depending on the hierarchy, for better human readability
    String inputFileName;
    int tokenNumber;
    
    String current; // variable to keep track of the current Token being processed
    int tokenType; //variable to keep track of the current Token type being processed
    
    int keyword; // the numeric code for the keyword, if token is of keyword type
    int intVal; // numeric code for the integerConstant, if token is of integerConstant type
    char symbol; //the char value of the symbol, it token is of symbol type
    //we do not use 'identifier' and 'stringVal' variables, as they are already handled by current
    	
	//constructor
	public CompilationEngine(File inputFile, File outputFile) {
        try {
            writer = new PrintWriter(outputFile);    
    		tokenizer = new Tokenizer(inputFile);
    		
    		inputFileName = inputFile.getName();
    		indentation = 0;
    		
    		current = tokenizer.getToken();
    		tokenType = tokenizer.getTokenType();
    		if (tokenType == KEYWORD)
    			keyword = tokenizer.getKeyword();
    		else if (tokenType == SYMBOL)
    			symbol = tokenizer.getSymbol();
    		else if (tokenType == INT_CONST)
    			intVal = tokenizer.getIntVal();
    		else {
    			System.out.println(String.format("Internal Error: invalid token parsed in file \"%s\" at line %d", inputFileName, tokenizer.getLine()));
    			System.exit(0);
    		}   		
    		
        } catch (IOException e) { e.printStackTrace(); }
	}
	
	/* The main CompilationEngine method */
	public void run() {
		while (tokenizer.hasMoreCommands()) { //checking if there is a valid current command
			//we always start with compiling the class, which handles the syntax analysis and calls other methods to handle, it as needed
			compileClass();
        }
		tokenizer.close();
	}
	
	private void compileClass() {
		printBody("<class>\n"); //a private function for printing text with the necessary indentation
		indentation++; //we increase the indentation every time we hand over the syntax analysis to another compileXXX() method
		
		eatKeyword(CLASS); //we check for and process the 'class' keyword
		eatIdentifier(); //we check for and process an identifier for className
		eatSymbol('{');	//we check for and process the opening curly bracket
		
		//as long as we keep encountering 'static' or 'field', it means that we have a class variable declaration, and we keep invoking compileClassVarDec
		while ( (tokenType == KEYWORD) && ((keyword == STATIC) || (keyword == FIELD)) ) {
			compileClassVarDec();
		}
		
		//as long as we keep encountering 'constructor', 'function' or 'method', it means that we have a class variable declaration, and we keep invoking subroutineDec
		while ( (tokenType == KEYWORD) && ((keyword == CONSTRUCTOR) || (keyword == FUNCTION) || (keyword == METHOD)) ) {
			compileSubroutineDec();
		}
		
		eatSymbol('}');	//we check for and process the closing curly bracket
		
		indentation--;			
		printBody("</class>\n");
	}
	
	private void compileClassVarDec() {
		printBody("<classVarDec>\n");
		indentation++;
		
		eatKeyword(STATIC, FIELD);
		
		//encapsulated code to check the type of the variable, consisting of calls to 
		//eatKeyword(char... args) for int, char and boolean, and to eatIdentifier() for className
		eatType();
		eatIdentifier();
		
		//while encountering commas, we keep processing the comma, together with an expected variable name
		while ( (tokenType == SYMBOL) && (symbol == ',') ) {
			eatSymbol(',');
			eatIdentifier();
		}
		
		eatSymbol(';');
		
		indentation--;	
		printBody("</classVarDec>\n");
	}
	
	private void compileSubroutineDec() {
		printBody("<subroutineDec>\n");
		indentation++;
		
		eatKeyword(CONSTRUCTOR, FUNCTION, METHOD); //process the type of subroutine
		
		//process a void, or int/char/boolean/className returning type for the subroutine
		if ((tokenType == KEYWORD) && (keyword == VOID))
			eatKeyword(VOID);
		else
			eatType();
		
		eatIdentifier(); //process an identifier for the subroutineName
		eatSymbol('(');
		compileParameterList();
		eatSymbol(')');
		compileSubroutineBody();
				
		indentation--;	
		printBody("</subroutineDec>\n");
	}	
	
	private void compileParameterList() {
		printBody("<parameterList>\n");
		indentation++;
		// ? -> if we have a token corresponding to a type declaration, the method starts processing the variable declarations
		if (((tokenType == KEYWORD) && ((keyword == INT) || (keyword == CHAR) || (keyword == BOOLEAN))) ||
				(tokenType == IDENTIFIER) ) {
			eatType(); //process the type
			eatIdentifier(); //process the variable name
			// * -> if we encounter a comma, it means we have multiple variables, and we keep processing them until no more commas are found
			while ( (tokenType == SYMBOL) && (symbol == ',') ) {
				eatSymbol(',');
				eatType();
				eatIdentifier();
			}
		}	
		indentation--;	
		printBody("</parameterList>\n");
	}	
	
	private void compileSubroutineBody() {
		printBody("<subroutineBody>\n");
		indentation++;
		
		eatSymbol('{');
		while ((tokenType == KEYWORD) && (keyword == VAR)) {
			compileVarDec();
		}		
		compileStatements();
		eatSymbol('}');
		
		indentation--;	
		printBody("</subroutineBody>\n");
		
	}	
	
	private void compileVarDec() {
		printBody("<varDec>\n");
		indentation++;
		
		eatKeyword(VAR);
		eatType();
		eatIdentifier();
		while ( (tokenType == SYMBOL) && (symbol == ',') ) {
			eatSymbol(',');
			eatIdentifier();
		}
		eatSymbol(';');
		
		indentation--;	
		printBody("</varDec>\n");
		
	}	
	
	private void compileStatements() {
		printBody("<statements>\n");
		indentation++;
		
		while ((tokenType == KEYWORD) && ((keyword == LET) || (keyword == IF)
				|| (keyword == WHILE) || (keyword == DO) || (keyword == RETURN))) {
			if (keyword == LET)
				compileLetStatement();
			else if (keyword == IF)
				compileIfStatement();
			else if (keyword == WHILE)
				compileWhileStatement();
			else if (keyword == DO)
				compileDoStatement();
			else //the last choice: if (keyword= = RETURN)
				compileReturnStatement();
		}
		
		indentation--;	
		printBody("</statements>\n");
	}	
	
	private void compileLetStatement() {
		printBody("<letStatement>\n");
		indentation++;
		
		eatKeyword(LET);
		eatIdentifier();
		if ((tokenType == SYMBOL) && (symbol == '[')) {
			eatSymbol('[');
			compileExpression();
			eatSymbol(']');
		}
		eatSymbol('=');
		compileExpression();
		eatSymbol(';');
		
		indentation--;	
		printBody("</letStatement>\n");
	}	
	
	private void compileIfStatement() {
		printBody("<ifStatement>\n");
		indentation++;
		
		eatKeyword(IF);
		eatSymbol('(');
		compileExpression();
		eatSymbol(')');
		eatSymbol('{');
		compileStatements();
		eatSymbol('}');
		if ((tokenType == KEYWORD) && (keyword == ELSE)) {
			eatKeyword(ELSE);
			eatSymbol('{');
			compileStatements();
			eatSymbol('}');
		}		
		indentation--;	
		printBody("</ifStatement>\n");
	}	
	
	private void compileWhileStatement() {
		printBody("<whileStatement>\n");
		indentation++;
		
		eatKeyword(WHILE);
		eatSymbol('(');
		compileExpression();
		eatSymbol(')');
		eatSymbol('{');
		compileStatements();
		eatSymbol('}');
		
		indentation--;	
		printBody("</whileStatement>\n");
	}	
	
	private void compileDoStatement() {
		printBody("<doStatement>\n");
		indentation++;
		
		eatKeyword(DO);
		//Subroutine Call
		eatIdentifier();
		if ( (tokenType == SYMBOL) && ((symbol == '(') || (symbol == '.')) ) {
			if (symbol == '(') {
				eatSymbol('(');
				compileExpressionList();
				eatSymbol(')');
			}
			else {
				eatSymbol('.');
				eatIdentifier();
				eatSymbol('(');
				compileExpressionList();
				eatSymbol(')');
			}
		}
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected symbol "
					+ "of type ( or . following subroutine call", inputFileName, tokenizer.getLine()));
			System.exit(0);
		}
		eatSymbol(';');
		
		indentation--;	
		printBody("</doStatement>\n");
	}
	
	private void compileReturnStatement() {
		printBody("<returnStatement>\n");
		indentation++;
		
		eatKeyword(RETURN);
		if (isExpression())
			compileExpression();
		eatSymbol(';');
		
		indentation--;	
		printBody("</returnStatement>\n");
	}	

	private void compileExpression() {
		printBody("<expression>\n");
		indentation++;
		
		compileTerm();
		while ((tokenType == SYMBOL) && contains(op, symbol) ) {
			eatSymbol(symbol);
			compileTerm();
		}
		
		indentation--;	
		printBody("</expression>\n");
	}	
	
	private void compileTerm() {
		printBody("<term>\n");
		indentation++;
		
		if (tokenType == INT_CONST)
			eatIntegerConstant();
		else if (tokenType == STRING_CONST)
			eatStringConstant();
		//if it is a keyword constant:
		else if (tokenType == KEYWORD) {
			if (keyword == TRUE)
				eatKeyword(TRUE);
			else if (keyword == FALSE) 
				eatKeyword(FALSE);
			else if (keyword == NULL)
				eatKeyword(NULL);
			else if (keyword == THIS)
				eatKeyword(THIS);
			else {
				System.out.println(String.format("Syntax Error in file \"%s\" at line %d, for term declaration "
						+ "as symbol: expected 'true', 'false', 'null' or 'this'", inputFileName, tokenizer.getLine()));
				System.exit(0); 		
			}
		}
		else if (tokenType == IDENTIFIER) {
			//if it is a variable array declaration:
			if ( (tokenizer.getToken2Type() == SYMBOL) && (tokenizer.getToken2().charAt(0) == '[') ) {
				eatIdentifier();
				eatSymbol('[');
				compileExpression();
				eatSymbol(']');
			}
			//if it is a subroutine call followed by parenthesis:
			else if ( (tokenizer.getToken2Type() == SYMBOL) && (tokenizer.getToken2().charAt(0) == '(') ) {
				eatIdentifier();
				eatSymbol('(');
				compileExpressionList();
				eatSymbol(')');
			}
			//if it is a subroutine from another class:
			else if ( (tokenizer.getToken2Type() == SYMBOL) && (tokenizer.getToken2().charAt(0) == '.') ) {
				eatIdentifier();
				eatSymbol('.');
				eatIdentifier();
				eatSymbol('(');
				compileExpressionList();
				eatSymbol(')');
			}
			//if it was just a variable:
			else
				eatIdentifier();
		}
		//if it is another expression:
		else if ((tokenType == SYMBOL) && (symbol == '(')) {
			eatSymbol('(');
			compileExpression();
			eatSymbol(')');
		}
		//if it is a unaryOp term:
		else if ((tokenType == SYMBOL) && ((symbol == '-') || (symbol == '~')) ) {
			eatSymbol(symbol);
			compileTerm();
		}
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d: invalid term declaration", inputFileName, tokenizer.getLine()));
			System.exit(0); 
		}

		indentation--;	
		printBody("</term>\n");
	}
	
	private void compileExpressionList() {
		printBody("<expressionList>\n");
		indentation++;
		
		if (isExpression()) {
			compileExpression();
			while ( (tokenType == SYMBOL) && (symbol == ',') ) {
				eatSymbol(',');
				compileExpression();
			}
		}
		
		indentation--;	
		printBody("</expressionList>\n");
	}	
	
	private void eatKeyword(int... correctKeywords) {
		if (tokenType == KEYWORD) {
			for (int correctKeyword: correctKeywords) {
				if (keyword == correctKeyword) {
					printToken(); 
					advance();
					return;
				}			
			}
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, incorrect keyword", inputFileName, tokenizer.getLine()));
			System.exit(0);
		}
		System.out.println(String.format("Syntax Error in file \"%s\" at line %d, incorrect token, expected keyword", inputFileName, tokenizer.getLine()));
		System.exit(0);
	}
	
	private void eatSymbol(char... correctSymbols) {
		if (tokenType == SYMBOL) {
			for (char correctSymbol : correctSymbols) {
				 if (symbol == correctSymbol) { 
					 printToken(); //a private method for printing the token after the required indentation
					 advance();
					 return;
				 }
			}
			System.out.println(String.format("Syntax Error: in file \"%s\" at line %d, incorrect symbol, expected '%s'", inputFileName, tokenizer.getLine(), symbol));
			System.exit(0);
		}
		System.out.println(String.format("Syntax Error: in file \"%s\" at line %d: incorrect token type, expected symbol: '%s'", inputFileName, tokenizer.getLine(), symbol));
		System.exit(0);
	}
	
	private void eatIntegerConstant() {
		if (tokenType == INT_CONST) {
			printToken();
			advance(); }
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected integer constant", inputFileName, tokenizer.getLine()));
			System.exit(0);
		}
	}
	
	private void eatStringConstant() {
		if (tokenType == STRING_CONST) {
			printToken();
			advance(); }
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected string constant", inputFileName, tokenizer.getLine()));
			System.exit(0);
		}
	}
	
	private void eatIdentifier() {
		if (tokenType == IDENTIFIER) {
			printToken();
			advance(); }
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected identifier", inputFileName, tokenizer.getLine()));
			System.exit(0);
		}
	}
	
	private void eatType() {
		if (tokenType == KEYWORD)
			eatKeyword(INT, CHAR, BOOLEAN);
		else if (tokenType == IDENTIFIER)
			eatIdentifier();
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected a type "
					+ "declaration (int, char, boolean, or a className)", inputFileName, tokenizer.getLine()));
			System.exit(0);
		}
	}
	

	private boolean isExpression() {
		if (tokenType == INT_CONST)
			return true;
		else if (tokenType == STRING_CONST)
			return true;
		else if (
				  (tokenType == KEYWORD) && 
				  ((keyword == TRUE) || (keyword == FALSE) || (keyword == NULL) || (keyword == THIS)) 
				 )
			return true;
		else if (tokenType == IDENTIFIER)
			return true;
		else if (
				 (tokenType == SYMBOL) &&
		  		 ((symbol == '(') || (symbol == '-') || (symbol == '~'))
		  		)
			return true;
		else
			return false;
	}
	
	private boolean contains(char[] opList, char symbolToCheck) {
		for (char c: opList)
			if (symbolToCheck == c)
				return true;
		return false;
	}
	
	private void printBody(String input) {
		for (index = 0; index < indentation; index ++) {
			writer.format(" ");
			System.out.print(" ");
		}
		writer.format(input);
		System.out.print(input);
	}
	
	private void printToken() {
		for (index = 0; index < indentation; index ++) {
			writer.format(" ");
			System.out.print(" ");
		}
		writer.format("<%s> %s </%s>\n", tokenizer.getTokenLabel(), tokenizer.getToken(), tokenizer.getTokenLabel());
		System.out.print(String.format("<%s> %s </%s>\n", tokenizer.getTokenLabel(), tokenizer.getToken(), tokenizer.getTokenLabel()));
	}
	
	//advances the token offered by the tokenizer, and updates associated variables
	private void advance() {
		tokenizer.advance();
		current = tokenizer.getToken();
		tokenType = tokenizer.getTokenType();
		if (tokenType == KEYWORD)
			keyword = tokenizer.getKeyword();
		else if (tokenType == SYMBOL)
			symbol = tokenizer.getSymbol();
		else if (tokenType == INT_CONST)
			intVal = tokenizer.getIntVal();
	}
	
	public void close() {
		writer.close();
	}
}