import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CompilationEngine {
	    
    char[] op = {'+', '-', '*', '/', '&', '|', '<', '>', '='}; //a list of all the symbols to make searching through them easier
    Map<Character, String> arithmeticOpMap; //a hash map to correlate a symbol directly with an arithmetic/logical operation
    
	Tokenizer tokenizer;
    PrintWriter writer;
    SymbolTable symbolTable;
    
   
    
    String inputFileName;
    int tokenNumber;
    
    String current; // variable to keep track of the current Token being processed
    int tokenType; //variable to keep track of the current Token type being processed
    
    int keyword; // the numeric code for the keyword, if token is of keyword type
    char symbol; //the char value of the symbol, it token is of symbol type
    //we do not use 'identifier' and 'stringVal' String variables, as they are already handled by current
    
    String currentClassName; //keeps track of the current class that we are compiling
    
    String currentSubroutineName; //keeps track of the name of the current subroutine that we are compiling
    int currentSubroutineType; //keeps track of the current subroutine's type (constructor, method, function)
	String currentSubroutineReturnType; //keeps track of the current subroutine's return type (void, int, char, boolean, className)
    
	String currentSubroutineVarName; //the name of the current subroutine local variable
	String currentSubroutineVarType; //the type of the current subroutine local variable

	int ifLabelCounter, whileLabelCounter; //label counters to insure unique labels for if and while statements
    	
	/*constructor*/
	public CompilationEngine(File inputFile, File outputFile) {
        try {
            writer = new PrintWriter(outputFile);    
    		tokenizer = new Tokenizer(inputFile);
    		symbolTable = new SymbolTable(inputFile);
    		
    		inputFileName = inputFile.getName();
    		arithmeticOpMap = Map.of('+', "add", 
    							  '-', "sub",
    							  '*', "call Math.multiply 2",
    							  '/', "call Math.divide 2",
    							  '=', "eq",
    							  '>', "gt", 
    							  '<', "lt", 
    							  '&', "and",
    							  '|', "or");
    		current = tokenizer.getToken();
    		tokenType = tokenizer.getTokenType();
    		if (tokenType == JackCompiler.KEYWORD)
    			keyword = tokenizer.getKeyword();
    		else if (tokenType == JackCompiler.SYMBOL)
    			symbol = tokenizer.getSymbol();
    		else {
    			System.out.println(String.format("Internal Error: invalid token parsed in file \"%s\" at line %d", inputFileName, tokenizer.getLine()));
    			writer.close();
    			System.exit(0);
    		}   		
        } catch (IOException e) { e.printStackTrace(); }
	}
	
	/* The main CompilationEngine method  */
	public void run() {
		while (tokenizer.hasMoreCommands()) { //checking if there is a valid current command
			//we always start with compiling the class, which handles the syntax analysis and calls other methods to handle it as needed
			compileClass();
        }
		tokenizer.close();
	}
	
	/*starts compiling a new class*/
	private void compileClass() {
		symbolTable.startClass(); //starts a new class symbolTable for keeping track of the variables' type, kind and number
		
		eatKeyword(JackCompiler.CLASS); //we check for the 'class' keyword
		currentClassName = inputFileName.substring(0, inputFileName.lastIndexOf(".jack"));
		//we check for and process an identifier for className, making sure it's the the same as the fileName
		eatIdentifier(currentClassName); 
		eatSymbol('{');	//we check for and process the opening curly bracket
		
		//as long as we keep encountering 'static' or 'field', it means that we have a class variable declaration, and we keep invoking compileClassVarDec
		while ( (tokenType == JackCompiler.KEYWORD) && ((keyword == JackCompiler.STATIC) || (keyword == JackCompiler.FIELD)) ) {
			compileClassVarDec();
		}
		
		//as long as we keep encountering 'constructor', 'function' or 'method', it means that we have a class variable declaration, and we keep invoking subroutineDec
		while ( (tokenType == JackCompiler.KEYWORD) && ((keyword == JackCompiler.CONSTRUCTOR) || (keyword == JackCompiler.FUNCTION) || (keyword == JackCompiler.METHOD)) ) {
			compileSubroutineDec();
		}
		
		eatSymbol('}');	//we check for and process the closing curly bracket

	}
	
	/* compiles the variable declarations of the current class and populates the class symbol table*/
	private void compileClassVarDec() {
		int classVarKind = keyword;
		eatKeyword(JackCompiler.STATIC, JackCompiler.FIELD); //a variable must either be of kind static or field

		String classVarType = current;
		eatType();
		
		String classVarName = current;
		eatIdentifier();
		
		symbolTable.define(classVarName, classVarType, classVarKind);
		//while encountering commas, we keep processing the comma, together with an expected variable name
		while ( (tokenType == JackCompiler.SYMBOL) && (symbol == ',') ) {
			eatSymbol(',');
			classVarName = current;
			eatIdentifier();
			symbolTable.define(classVarName, classVarType, classVarKind);
		}
		eatSymbol(';');
	}
	
	/* handles the declaration of a new subroutine*/
	private void compileSubroutineDec() { 
		symbolTable.startSubroutine(); //starts a new subroutine symbolTable
		//for readability, we reinitialise the counters to 0 each time we start a new subroutine, 
		//because the VM translator will still ensure a unique label using the class + subroutine name combination
		ifLabelCounter = 0;
		whileLabelCounter = 0;
		
		currentSubroutineType = keyword; //keeps track if the subroutine is a constructor, function or method
		eatKeyword(JackCompiler.CONSTRUCTOR, JackCompiler.FUNCTION, JackCompiler.METHOD); //process the type of subroutine, making sure it's one of those 3

		currentSubroutineReturnType = current;
		//if the subroutine is a constructor, we make sure that its return type is the current class name
		if (currentSubroutineType == JackCompiler.CONSTRUCTOR)
			eatIdentifier(currentClassName);
		//if not, it can be of type void, or int/char/boolean/className returning type (for the subroutine of type method or function)
		else { 
			if ((tokenType == JackCompiler.KEYWORD) && (keyword == JackCompiler.VOID))
				eatKeyword(JackCompiler.VOID);
			else
				eatType();
		}
		
		currentSubroutineName = current;
		eatIdentifier(); //process an identifier for the subroutineName
		
		eatSymbol('(');
		compileParameterList(); //compile the list of parameters/argument variables
		eatSymbol(')');
		
		compileSubroutineBody();	

	}	
	
	private void compileParameterList() {
		//if we have a subroutine of type method, we add an argument 0 which will later become the base address of the object 
		//we are working on, after the caller pushes that address first before the other arguments, and then calling this method
		if (currentSubroutineType == JackCompiler.METHOD) 
			symbolTable.define("this", currentClassName, JackCompiler.ARG);
		
		currentSubroutineVarType = current;
		//if the return type is a keyword, make sure that is of type int, char or boolean
		if (((tokenType == JackCompiler.KEYWORD) && ((keyword == JackCompiler.INT) || (keyword == JackCompiler.CHAR) || (keyword == JackCompiler.BOOLEAN))) ||
				(tokenType == JackCompiler.IDENTIFIER) ) { //otherwise it is assumed to be a className type
			eatType(); //process the type
			
			currentSubroutineVarName = current;
			eatIdentifier(); //process the variable name
			
			//before moving on to other arguments/parameters, we define it in the subroutine's symbolTable
			symbolTable.define(currentSubroutineVarName, currentSubroutineVarType, JackCompiler.ARG);
			
			//if we encounter a comma, it means we have multiple variables of that t, and we keep processing them until no more commas are found
			while ( (tokenType == JackCompiler.SYMBOL) && (symbol == ',') ) {
				eatSymbol(',');
				
				currentSubroutineVarType = current;
				eatType();
				currentSubroutineVarName = current;
				eatIdentifier();
				
				symbolTable.define(currentSubroutineVarName, currentSubroutineVarType, JackCompiler.ARG);
			}
		}	
	}	
	
	private void compileSubroutineBody() {
		eatSymbol('{');
		//if we encounter a var keyword, it means we have more local variables, and we keep processing them
		while ((tokenType == JackCompiler.KEYWORD) && (keyword == JackCompiler.VAR)) {
			compileVarDec();
		}
		//after declaring (and counting in the symbolTable) all the local arguments, and before compiling the statements, 
		//we need to declare in VM code that a function needing that many nVars will follow
		writer.format("function %s.%s %d\n", currentClassName, currentSubroutineName, symbolTable.VarCount(JackCompiler.VAR));
		
		//when compiling a *method* subroutine:		
		if (currentSubroutineType == JackCompiler.METHOD)
			//we need to make sure that argument 0, which will be the base address of the object, will have been pushed, 
			//then popped as pointer 0, in order for the method to access the object's fields	
			writer.print("push argument 0\n" 
					   + "pop pointer 0\n");
		//when compiling a *constructor* subroutine:		
		else if (currentSubroutineType == JackCompiler.CONSTRUCTOR)
			writer.format("push constant %d\n"     //push the number of field variables that we need to allocate memory to 
						+ "call Memory.alloc 1\n"  //Memory.alloc will allocate memory to that many 16-bit words, and return the address of the to-be object
						+ "pop pointer 0\n",       //we pop that address into pointer 0, as the constructor function starts manipulating the object's fields
						symbolTable.VarCount(JackCompiler.FIELD));
		
		compileStatements(); 
		eatSymbol('}');
	}	
	
	//compiles all the local variable declarations 
	private void compileVarDec() {
		eatKeyword(JackCompiler.VAR);
		
		currentSubroutineVarType = current;
		eatType();
		
		currentSubroutineVarName = current;
		eatIdentifier();
		
		symbolTable.define(currentSubroutineVarName, currentSubroutineVarType, JackCompiler.VAR);
		
		//similar to the class, we keep compiling the to-be local variables until no more commas are encoutnered
		while ( (tokenType == JackCompiler.SYMBOL) && (symbol == ',') ) {
			eatSymbol(',');
			
			currentSubroutineVarName = current;
			eatIdentifier();
			
			symbolTable.define(currentSubroutineVarName, currentSubroutineVarType, JackCompiler.VAR);
		}
		eatSymbol(';');
	}	
	
	private void compileStatements() {
		//we keep compiling different statements until we stop encountering a keyword for that statement
		while ((tokenType == JackCompiler.KEYWORD) && 
			   ((keyword == JackCompiler.LET) || (keyword == JackCompiler.IF) || (keyword == JackCompiler.WHILE) || (keyword == JackCompiler.DO) || (keyword == JackCompiler.RETURN)) ) {

			if (keyword == JackCompiler.LET)
				compileLetStatement();
			else if (keyword == JackCompiler.IF)
				compileIfStatement();
			else if (keyword == JackCompiler.WHILE)
				compileWhileStatement();
			else if (keyword == JackCompiler.DO)
				compileDoStatement();
			else //the last choice: RETURN
				compileReturnStatement();
		}
	}	
	
	private void compileLetStatement() {
		eatKeyword(JackCompiler.LET);
		
		String assignmentVariable = current; //we keep track of the variable that is to be assigned
		eatIdentifier();
		
		//if we have an array assignment
		if ((tokenType == JackCompiler.SYMBOL) && (symbol == '[')) { 
			WritePushPop("push", assignmentVariable);//we push the address of the array
			
			eatSymbol('[');
			compileExpression(); //compile the expression1 in-between the brackets
			eatSymbol(']');
			
			//we add the resulting value from he expression to the previously pushed address of the array
			writer.format("add\n"); 
			
			eatSymbol('=');
			compileExpression(); //compile the expression2 on the right side of the equal sign
			
			writer.format("pop temp 0\n" //we pop the resulting value into a temporary value, in case expression 2 used an array
						+ "pop pointer 1\n" //we pop the address of assignmentVariable[expression1] into pointer 1
						+ "push temp 0\n" // we again push the saved value of expression 2
						+ "pop that 0\n"); //we pop it into the RAM location that address assignmentVariable[expression1] points to
		}
		//otherwise, we have a normal variable assignment
		else { 
			eatSymbol('=');
			compileExpression();
			WritePushPop("pop", assignmentVariable);
		}
		eatSymbol(';');
	}	
	
	//implemented the nandToTetris' compiler's labelling logic (not the the one recommended in the course) for easier VM code readability, despite adding an extra label
	private void compileIfStatement() {
		int thisIfLabelCounter = ifLabelCounter;
		ifLabelCounter++;
		
		eatKeyword(JackCompiler.IF);
		eatSymbol('(');
		compileExpression(); //the checked expression
		eatSymbol(')');
		
		writer.format("if-goto IF_TRUE%d\n" //pertains to the checked expression being TRUE - statements 1 will be run
					+ "goto IF_FALSE%d\n" //pertains to the checked expression being FALSE - statements 2 will be run
					+ "label IF_TRUE%d\n", thisIfLabelCounter, thisIfLabelCounter, thisIfLabelCounter); //pertains to the checked expression being TRUE,
																										//with an else statement being either present or absent
		eatSymbol('{');
		compileStatements(); //statements 1
		eatSymbol('}');		
		
		//different labelling logic, depending if we have an if, or an if-else statement
		if ((tokenType == JackCompiler.KEYWORD) && (keyword == JackCompiler.ELSE)) {
			writer.format("goto IF_END%d\n" //pertains to the checked expression being TRUE, program jumps at the end and skips statements 2 
						+ "label IF_FALSE%d\n", thisIfLabelCounter, thisIfLabelCounter); //pertains to the checked expression being FALSE in case an else statement IS present
			
			eatKeyword(JackCompiler.ELSE);
			eatSymbol('{');
			compileStatements(); //statements 2
			eatSymbol('}');
			
			writer.format("label IF_END%d\n", thisIfLabelCounter); //pertains to the checked expression being TRUE
		}
		else
			writer.format("label IF_FALSE%d\n", thisIfLabelCounter); //pertains to the checked expression being FALSE in case NO else statement is present
	}	
	
	//implemented the nandToTetris' course (and compiler) labelling logic
	private void compileWhileStatement() {
		int thisWhileLabelCounter = whileLabelCounter;
		whileLabelCounter++;
		
		eatKeyword(JackCompiler.WHILE);
		writer.format("label WHILE_EXP%d\n", thisWhileLabelCounter); //WHILE_EXPRESSION
		
		eatSymbol('(');
		compileExpression();
		eatSymbol(')');
		
		writer.print("not\n");
		writer.format("if-goto  WHILE_END%d\n", thisWhileLabelCounter);
		
		eatSymbol('{');
		compileStatements();
		eatSymbol('}');
		
		writer.format("goto WHILE_EXP%d\n", thisWhileLabelCounter);
		writer.format("label  WHILE_END%d\n", thisWhileLabelCounter);
	}	
	
	private void compileDoStatement() {
		eatKeyword(JackCompiler.DO);
		compileSubroutineCall();
		
		writer.print("pop temp 0\n"); //we discard the return value of the function in the case of a DO statement
		eatSymbol(';');
	}
	
	/*implemented an extra private subroutine,to be used by both compileDoStatement and compileTerm in order to avoid code repetition*/
	private void compileSubroutineCall() {
		//if it is a subroutine call from the current class, followed by parentheses:
				if ( (tokenizer.getToken2Type() == JackCompiler.SYMBOL) && (tokenizer.getToken2().charAt(0) == '(') ) {
					String subroutineName = current;
					eatIdentifier();
					
					// we push pointer 0, so that the method has access to the address of this object
					writer.format("push pointer 0\n"); 
					
					eatSymbol('(');
					int nArgs = compileExpressionList(); //compileExpressionList() also returns the number of compiled expressions, 
														 //to keep track of how many parameters/arguments the function call will use
					eatSymbol(')');
					
					//we increase nArgs by one so that we account for the object's reference having been pushed as the first argument (above)
					writer.format("call %s.%s %d\n", currentClassName, subroutineName, nArgs + 1); 
				}
				//if it is a subroutine from another class, OR a subroutine called on another object
				else if ( (tokenizer.getToken2Type() == JackCompiler.SYMBOL) && (tokenizer.getToken2().charAt(0) == '.') ) {
					
					if (symbolTable.KindOf(current) == JackCompiler.NONE) { //if the identifier is not recognised, it is assumed to be a class name: we have a function call
						String className = current;
						eatIdentifier();
						
						eatSymbol('.');
						
						String subroutineName = current;
						eatIdentifier();
						
						eatSymbol('(');
						int nArgs  = compileExpressionList();
						eatSymbol(')');
						
						writer.format("call %s.%s %d\n", className, subroutineName, nArgs); // no object reference required for a function call
					}
					else { // the identifier is recognised as a variable name, therefore this is a method acting upon that [variable name] object
						String varName = current;
						String className = symbolTable.TypeOf(current);
						
						WritePushPop("push", varName); // we push the variable itself as the first argument for the function call
						
						eatIdentifier();
						eatSymbol('.');
						
						String subroutineName = current;
						eatIdentifier();
						
						eatSymbol('(');
						int nArgs  = compileExpressionList();
						eatSymbol(')');
						
						writer.format("call %s.%s %d\n", className, subroutineName, nArgs + 1); //again, we account for the extra reference pushed as the first parameter/argument
					}
				}
				
				else {
					System.out.println(String.format("Syntax Error in file \"%s\" at line %d, incorrect subroutineCall declaration "
							, inputFileName, tokenizer.getLine()));
					writer.close();
					System.exit(0);
				}
	}
	
	private void compileReturnStatement() {
		eatKeyword(JackCompiler.RETURN);
		
		if (currentSubroutineType == JackCompiler.CONSTRUCTOR) { //if we have a constructor, then it MUST return "this", the address of the new object
			eatKeyword(JackCompiler.THIS);
			writer.print("push pointer 0\n" //constructor pushes the reference of the object it's constructing in order to return it
					   + "return\n");
			eatSymbol(';');
			return;
		}
		
		if (currentSubroutineReturnType == "void") {
			if (isExpression()) {
				System.out.println(String.format("Syntax Error in file \"%s\" at line %d, return value "
						+ "specified despite subroutine returning void", inputFileName, tokenizer.getLine()));
				writer.close();
				System.exit(0);
			}
			else {
				writer.print("push constant 0\n" //void subroutines push a dummy value before returning
						   + "return\n");
			}	
		}
		else { //if the current subroutine return type if NOT void
			if (isExpression()) {
				compileExpression();
				writer.print("return\n");
			}
			else {
				System.out.println(String.format("Syntax Error in file \"%s\" at line %d, no return value "
						+ "specified for a non-void subroutine", inputFileName, tokenizer.getLine()));
				writer.close();
				System.exit(0);
			}
		}
		eatSymbol(';');
	}	

	private void compileExpression() {
		compileTerm();
		while ((tokenType == JackCompiler.SYMBOL) && contains(op, symbol) ) {//we keep compiling terms while we keep encountering op symbols
			char currentSymbol = symbol;
			eatSymbol(symbol);
			
			compileTerm();
			
			WriteArithmetic(currentSymbol);
		}
	}	
	
	private void compileTerm() {
		
		if (tokenType == JackCompiler.INT_CONST) {
			String currentInt = current;
			eatIntegerConstant();
			writer.format("push constant %s\n", currentInt);
		}
		else if (tokenType == JackCompiler.STRING_CONST) {
			writer.format("push constant %d\n"
						+ "call String.new 1\n", current.length());
			for (char c: current.toCharArray()) {
				writer.format("push constant %d\n"
							+ "call String.appendChar 2\n", (int) c);
			}
			eatStringConstant();
		}
		
		//if it is a keyword constant, like true, false, null or this
		else if (tokenType == JackCompiler.KEYWORD) {
			if (keyword == JackCompiler.TRUE) {
				writer.print("push constant 0\n"
						   + "not\n");
				eatKeyword(JackCompiler.TRUE);
			}
			else if ((keyword == JackCompiler.FALSE) || (keyword == JackCompiler.NULL)) {
				writer.print("push constant 0\n");
				eatKeyword(JackCompiler.FALSE, JackCompiler.NULL);
			}
			else if (keyword == JackCompiler.THIS) {
				writer.print("push pointer 0\n");
				eatKeyword(JackCompiler.THIS);
			}
			else {
				System.out.println(String.format("Syntax Error in file \"%s\" at line %d, for term declaration "
						+ "as symbol: expected 'true', 'false', 'null' or 'this'", inputFileName, tokenizer.getLine()));
				writer.close();
				System.exit(0); 		
			}
		}
		
		//if the term starts with a variable's name:
		else if (tokenType == JackCompiler.IDENTIFIER) {
			//if it is a variable array declaration:
			if ( (tokenizer.getToken2Type() == JackCompiler.SYMBOL) && (tokenizer.getToken2().charAt(0) == '[') ) {
				WritePushPop("push", current);
				eatIdentifier();
				
				eatSymbol('[');
				compileExpression();
				eatSymbol(']');
				
				writer.print("add\n" //the value of the compiled expression is added to the value of the variable's base address
						   + "pop pointer 1\n" //it is then popped into the THAT segment
						   + "push that 0\n"); //the value of the array at the [expression] index is then pushed onto the stack
			}
			//if it is a subroutine call upon that variable:
			else if ( (tokenizer.getToken2Type() == JackCompiler.SYMBOL) && ((tokenizer.getToken2().charAt(0) == '(') || (tokenizer.getToken2().charAt(0) == '.')) )
				compileSubroutineCall();
			//if it was just a primitive or reference variable:
			else {
				WritePushPop("push", current);
				eatIdentifier();
			}
		}
		
		//if it is another expression:
		else if ((tokenType == JackCompiler.SYMBOL) && (symbol == '(')) {
			eatSymbol('(');
			compileExpression();
			eatSymbol(')');
		}
		
		//if the term starts with an unaryOp symbol:
		else if ((tokenType == JackCompiler.SYMBOL) && ((symbol == '-') || (symbol == '~')) ) {
			char thisSymbol = symbol;
			eatSymbol(symbol);
			compileTerm();
			
			if (thisSymbol == '-')
				writer.print("neg\n");
			else 
				writer.print("not\n");
		}
		
		//if no condition was satisfied, as per the Jakc syntax:
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d: invalid term declaration", inputFileName, tokenizer.getLine()));
			writer.close();
			System.exit(0); 
		}
	}
	
	private int compileExpressionList() {
		int noOfExpressions = 0; // the number of compiler expressions is returned, to be used by compileSubroutineCall()
		
		if (isExpression()) {
			compileExpression();
			noOfExpressions ++;
			
			while ( (tokenType == JackCompiler.SYMBOL) && (symbol == ',') ) {
				eatSymbol(',');
				compileExpression();
				noOfExpressions ++;
			}
		}
		return noOfExpressions;
	}	
	
	/*processes an int keyword, accepting as arguments a list of valid keywords*/
	private boolean eatKeyword(int... correctKeywords) {
		if (tokenType == JackCompiler.KEYWORD) {
			for (int correctKeyword: correctKeywords) {
				if (keyword == correctKeyword) {
					advance();
					return true;
				}			
			}
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, incorrect keyword", inputFileName, tokenizer.getLine()));
			writer.close();
			System.exit(0);
			return false;
		}
		System.out.println(String.format("Syntax Error in file \"%s\" at line %d, incorrect token, expected keyword", inputFileName, tokenizer.getLine()));
		writer.close();
		System.exit(0);
		return false;
	}
	
	/*processes a char symbol, accepting as arguments a list of valid symbols*/
	private boolean eatSymbol(char... correctSymbols) {
		if (tokenType == JackCompiler.SYMBOL) {
			for (char correctSymbol : correctSymbols) {
				 if (symbol == correctSymbol) { 
					 advance();
					 return true;
				 }
			}
			System.out.println(String.format("Syntax Error: in file \"%s\" at line %d, incorrect symbol, expected one of: ", inputFileName, tokenizer.getLine()));
			System.out.println(correctSymbols);
			writer.close();
			System.exit(0);
			return false;
		}
		
		System.out.println(String.format("Syntax Error: in file \"%s\" at line %d: incorrect token type, expected symbol, one of following: ", inputFileName, tokenizer.getLine(), symbol));
		System.out.println(correctSymbols);
		writer.close();
		System.exit(0);
		return false;
	}
	
	/* processes an integer constant */
	private boolean eatIntegerConstant() {
		if (tokenType == JackCompiler.INT_CONST) {
			advance();
			return true;}
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected integer constant", inputFileName, tokenizer.getLine()));
			writer.close();
			System.exit(0);
			return false;
		}
	}
	
	/* processes a string constant*/
	private boolean eatStringConstant() {
		if (tokenType == JackCompiler.STRING_CONST) {
				advance(); 
				return true;
			}
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected string constant", inputFileName, tokenizer.getLine()));
			writer.close();
			System.exit(0);
			return false;
		}
	}
	
	/*processes a String identifier, checking against a list of valid identifiers*/
	private boolean eatIdentifier(String... correctIdentifiers) {
		if (tokenType == JackCompiler.IDENTIFIER) {
			if (correctIdentifiers.length == 0) {
				advance();	
				return true;
			}
			else if (correctIdentifiers.length != 0) {
				for (String correctIdentifier : correctIdentifiers) {
					if (current.equals(correctIdentifier)) {
						advance();	
						return true;
					}
				}
				System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected one of following identifiers:", inputFileName, tokenizer.getLine()));
				System.out.println(correctIdentifiers);
				writer.close();
				System.exit(0);
				return false;
			}
		}
		System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected identifier", inputFileName, tokenizer.getLine()));
		System.exit(0);
		writer.close();
		return false;
	}
	
	/* process a token that is to be a type */
	private boolean eatType() {
		if (tokenType == JackCompiler.KEYWORD)
			return eatKeyword(JackCompiler.INT, JackCompiler.CHAR, JackCompiler.BOOLEAN);
		else if (tokenType == JackCompiler.IDENTIFIER)
			return eatIdentifier();
		else {
			System.out.println(String.format("Syntax Error in file \"%s\" at line %d, expected a type "
					+ "declaration (int, char, boolean, or a className)", inputFileName, tokenizer.getLine()));
			writer.close();
			System.exit(0);
			return false;
		}
	}
	
	/* returns true if we have the start of an expression, otherwise false*/
	private boolean isExpression() {
		if (tokenType == JackCompiler.INT_CONST)
			return true;
		else if (tokenType == JackCompiler.STRING_CONST)
			return true;
		else if (
				  (tokenType == JackCompiler.KEYWORD) && 
				  ((keyword == JackCompiler.TRUE) || (keyword == JackCompiler.FALSE) || (keyword == JackCompiler.NULL) || (keyword == JackCompiler.THIS)) 
				 )
			return true;
		else if (tokenType == JackCompiler.IDENTIFIER)
			return true;
		else if (
				 (tokenType == JackCompiler.SYMBOL) &&
		  		 ((symbol == '(') || (symbol == '-') || (symbol == '~'))
		  		)
			return true;
		else
			return false;
	}
	
	/* returns true if the char list opList contains the char symbolToCheck, otherwise false */
	private boolean contains(char[] opList, char symbolToCheck) {
		for (char c: opList)
			if (symbolToCheck == c)
				return true;
		return false;
	}
	
	
	//advances the token offered by the tokenizer, and updates associated variables
	private void advance() {
		tokenizer.advance();
		current = tokenizer.getToken();
		tokenType = tokenizer.getTokenType();
		if (tokenType == JackCompiler.KEYWORD)
			keyword = tokenizer.getKeyword();
		else if (tokenType == JackCompiler.SYMBOL)
			symbol = tokenizer.getSymbol();
	}
	
	/* writes a push or pop command that uses the variable's kind and index from the symbolTable */
	private void WritePushPop(String pushPop, String varName) {
		
		switch (symbolTable.KindOf(varName)) {
		case JackCompiler.STATIC:
			writer.format("%s static %d\n", pushPop, symbolTable.IndexOf(varName));
			return;
		case JackCompiler.FIELD:
			writer.format("%s this %d\n", pushPop, symbolTable.IndexOf(varName));
			return;
		case JackCompiler.ARG:
			writer.format("%s argument %d\n", pushPop, symbolTable.IndexOf(varName));
			return;
		case JackCompiler.VAR:
			writer.format("%s local %d\n", pushPop, symbolTable.IndexOf(varName));
			return;
		default:
			if (varName == "this")
				writer.format("%s pointer 0\n", pushPop);
			else
				System.out.println(String.format("Error in file \"%s\" at line %d, varName %s not found in class/subroutine declaration", 
						inputFileName, tokenizer.getLine(), varName));
		}
	}
	
	/* writes an arithmetic operation in VM code, by using the symbol character from the high-level code as input*/
	private void WriteArithmetic(char expressionSymbol) {
		writer.format("%s\n", arithmeticOpMap.get(expressionSymbol));
	}
	
	/* closes the writer, allowing the file to be written from the buffer */
	public void close() {
		writer.close();
	}
	
}
